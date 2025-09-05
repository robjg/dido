package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaFactory;
import dido.data.SchemaField;
import dido.data.schema.DataSchemaFactory;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * How to read CSV Data In.
 */
public class DataInCsv implements DataInHow<Reader> {

    private final CSVFormat csvFormat;

    private final DataSchema schema;

    private final boolean withHeader;

    private final boolean partialSchema;

    private final DidoConversionProvider conversionProvider;

    private DataInCsv(Settings settings) {
        this.csvFormat = Objects.requireNonNullElse(settings.csvFormat, CSVFormat.DEFAULT);
        this.schema = settings.schema;
        this.withHeader = settings.withHeader;
        this.partialSchema = settings.partialSchema;
        this.conversionProvider = Objects.requireNonNullElse(settings.conversionProvider,
                DefaultConversionProvider.defaultInstance());
    }

    public static class Settings {

        private CSVFormat csvFormat;

        private DataSchema schema;

        private boolean withHeader;

        private boolean partialSchema;

        private DidoConversionProvider conversionProvider;

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

        public Settings partialSchema(DataSchema schema) {
            this.schema = schema;
            this.partialSchema = true;
            return this;
        }

        public Settings conversionProvider(DidoConversionProvider converter) {
            this.conversionProvider = converter;
            return this;
        }

        public DataIn fromPath(Path path) {
            try {
                return make().inFrom(Files.newBufferedReader(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataIn fromInputStream(InputStream inputStream) {
            return make().inFrom(new BufferedReader(new InputStreamReader(inputStream)));
        }

        public DataIn fromReader(Reader reader) {
            return make().inFrom(reader);
        }

        public DataInCsv make() {
            return new DataInCsv(this);
        }

        public Function<String, DidoData> mapFromString() {

            DataInCsv dataInCsv = make();

            return s -> {
                try (DataIn in = dataInCsv.inFrom(new StringReader(s))) {
                    return in.stream().findFirst().orElse(null);
                }
            };
        }

    }

    public static DataIn fromPath(Path path) {

        return with().fromPath(path);
    }

    public static DataIn fromReader(Reader reader) {

        return with().fromReader(reader);
    }

    public static DataIn fromInputStream(InputStream inputStream) {

        return with().fromInputStream(inputStream);
    }

    public static Function<String, DidoData> mapFromString() {

        return with().mapFromString();
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<Reader> getInType() {
        return Reader.class;
    }

    @Override
    public DataIn inFrom(Reader reader) {

        CSVFormat csvFormat = this.csvFormat;

        DataSchema schema;
        CSVParser csvParser;
        Iterator<CSVRecord> iterator;

        final Function<CSVRecord, CsvData> wrapperFunction;

        try {

            csvParser = csvFormat.parse(reader);
            iterator = csvParser.iterator();

            if (this.schema == null || this.partialSchema) {

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
                        // We've read a row already, now we need an iterator that will return it again for the data
                        iterator = new OneAheadIterator<>(iterator, record);
                    } else {
                        schema = DataSchema.emptySchema();
                    }
                }
                wrapperFunction = CsvData.wrapperFunctionFor(schema, conversionProvider);

            } else {
                schema = this.schema;
                if (this.withHeader && iterator.hasNext()) {
                        CSVRecord record = iterator.next();
                        wrapperFunction = CsvData.wrapperFunctionFor(schema,
                                record.values(), conversionProvider);
                }
                else {
                    wrapperFunction = CsvData.wrapperFunctionFor(schema, conversionProvider);
                }
            }
        } catch (IOException e) {
            throw DataException.of(e);
        }

        final Iterator<CSVRecord> finalIterator = iterator;

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
