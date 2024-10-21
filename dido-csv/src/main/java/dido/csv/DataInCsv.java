package dido.csv;

import dido.data.*;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * How to read CSV Data In.
 */
public class DataInCsv implements DataInHow<InputStream> {

    private final CSVFormat csvFormat;

    private final DataSchema schema;

    private final boolean withHeader;

    private final boolean partialSchema;

    private final DidoConversionProvider converter;

    public static class Settings {

        private CSVFormat csvFormat;

        private DataSchema schema;

        private boolean withHeader;

        private boolean partialSchema;

        private DidoConversionProvider converter;

        public Settings csvFormat(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings header(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

        public Settings partialSchema(boolean partialSchema) {
            this.partialSchema = partialSchema;
            return this;
        }

        public Settings converter(DidoConversionProvider converter) {
            this.converter = converter;
            return this;
        }

        public DataIn fromFile(Path path) throws IOException {
            return make().inFrom(Files.newInputStream(path));
        }

        public DataIn from(InputStream inputStream) {
            return make().inFrom(inputStream);
        }

        public DataInCsv make() {
            return new DataInCsv(this);
        }

    }

    private DataInCsv(Settings settings) {
        this.csvFormat = Objects.requireNonNullElse(settings.csvFormat, CSVFormat.DEFAULT);
        this.schema = settings.schema;
        this.withHeader = settings.withHeader;
        this.partialSchema = settings.partialSchema;
        this.converter = Objects.requireNonNullElse(settings.converter,
                DefaultConversionProvider.defaultInstance());
    }

    public static DataIn fromInputStream(InputStream inputStream) {

        return DataInCsv.withDefaults()
                .inFrom(inputStream);
    }

    public static DataIn fromPath(Path path) {

        try {
            return fromInputStream(Files.newInputStream(path));
        } catch (IOException e) {
            throw DataException.of(e);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static DataInHow<InputStream> withDefaults() {
        return new Settings().make();
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn inFrom(InputStream inputStream) {

        CSVFormat csvFormat = this.csvFormat;

        DataSchema schema;
        CSVParser csvParser;
        Iterator<CSVRecord> iterator;

        try {
            if (this.schema == null || this.partialSchema) {

                csvParser = csvFormat.parse(new InputStreamReader(inputStream));
                iterator = csvParser.iterator();

                if (this.withHeader || this.partialSchema) {
                    if (iterator.hasNext()) {
                        schema = schemaFromHeader(iterator.next(), this.partialSchema ? this.schema : null);
                    } else {
                        throw DataException.of("No Header Record.");
                    }
                } else {
                    if (iterator.hasNext()) {
                        CSVRecord record = iterator.next();
                        schema = schemaNoHeader(record);
                        iterator = new OneAheadIterator<>(iterator, record);
                    } else {
                        schema = DataSchema.emptySchema();
                    }
                }
            } else {
                schema = this.schema;
                if (this.withHeader) {
                    csvFormat = csvFormat.withFirstRecordAsHeader();
                }
                csvParser = csvFormat.parse(new InputStreamReader(inputStream));
                iterator = csvParser.iterator();
            }
        } catch (IOException e) {
            throw DataException.of(e);
        }

        final Iterator<CSVRecord> finalIterator = iterator;

        Function<CSVRecord, CsvData> wrapperFunction =
                CsvData.wrapperFunctionFor(schema, converter);

        return new DataIn() {

            @Override
            public Iterator<DidoData> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return finalIterator.hasNext();
                    }

                    @Override
                    public DidoData next() {
                        return wrapperFunction.apply(finalIterator.next());
                    }
                };
            }

            @Override
            public void close() {
                try {
                    csvParser.close();
                } catch (IOException e) {
                    throw DataException.of(e);
                }
            }
        };
    }

    static DataSchema schemaNoHeader(CSVRecord record) {
        SchemaFactory schemaBuilder = DataSchemaFactory.newInstance();

        for (String ignored : record) {
            schemaBuilder.addSchemaField(SchemaField.of(0, null, String.class));
        }
        return schemaBuilder.toSchema();
    }

    static DataSchema schemaFromHeader(CSVRecord record, DataSchema partialSchema) {
        SchemaFactory schemaBuilder = DataSchemaFactory.newInstance();

        for (String field : record) {
            schemaBuilder.addSchemaField(SchemaField.of(0, field, String.class));
        }
        if (partialSchema != null) {
            schemaBuilder.merge(partialSchema);
        }
        return schemaBuilder.toSchema();
    }

    static class OneAheadIterator<E> implements Iterator<E> {

        private final Iterator<E> original;

        private E current;

        OneAheadIterator(Iterator<E> original, E current) {
            this.original = original;
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            try {
                return current;
            } finally {
                if (original.hasNext()) {
                    current = original.next();
                } else {
                    current = null;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("CSV");
        if (this.schema != null) {
            if (partialSchema) {
                builder.append(", with partial schema");
            } else {
                builder.append(", with schema");
            }
        } else {
            builder.append(", with no schema");
        }
        if (withHeader) {
            builder.append(" and header");
        } else {
            builder.append(" and no header");
        }
        return builder.toString();
    }
}
