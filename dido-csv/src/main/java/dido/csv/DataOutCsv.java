package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.util.FieldValuesOut;
import dido.how.CloseableConsumer;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * How to write CSV Data Out.
 */
public class DataOutCsv implements DataOutHow<Appendable> {

    private final CSVFormat csvFormat;

    private final DataSchema schema;

    private final boolean withHeader;

    public static class Settings {

        private CSVFormat csvFormat;

        private DataSchema schema;

        private boolean withHeader;

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

        public DataOut toAppendable(Appendable appendable) {
            return make().outTo(appendable);
        }

        public DataOut toWriter(Writer writer) {
            return make().outTo(writer);
        }

        public DataOut toPath(Path path) {
            try {
                return make().outTo(Files.newBufferedWriter(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataOut toOutputStream(OutputStream outputStream) {

            return make().outTo(new OutputStreamWriter(outputStream));
        }

        public DataOutCsv make() {
            return new DataOutCsv(this);
        }

        public Function<DidoData, String> mapToString() {

            DataOutCsv dataOutCsv =
                    csvFormat(Objects.requireNonNullElse(csvFormat, CSVFormat.DEFAULT)
                            .builder()
                            .setRecordSeparator("")
                            .build())
                            .make();

            return data -> {
                StringBuilder result = new StringBuilder();

                try (DataOut out = dataOutCsv.outTo(result)) {
                    out.accept(data);
                }

                return result.toString();
            };
        }
    }

    private DataOutCsv(Settings settings) {
        this.csvFormat = Objects.requireNonNullElse(settings.csvFormat, CSVFormat.DEFAULT);
        this.schema = settings.schema;
        this.withHeader = settings.withHeader;
    }

    public static DataOut toAppendable(Appendable appendable) {
        return with().toAppendable(appendable);
    }

    public static DataOut toWriter(Writer writer) {
        return with().toWriter(writer);
    }

    public static DataOut toPath(Path path) {
        return with().toPath(path);
    }

    public static DataOut toOutputStream(OutputStream outputStream) {

        return with().toOutputStream(outputStream);
    }

    public static Function<DidoData, String> mapToString() {
        return with().mapToString();
    }

    public static Settings with() {
        return new Settings();
    }

    public static DataOutHow<Appendable> withDefaults() {
        return new Settings().make();
    }

    @Override
    public Class<Appendable> getOutType() {
        return Appendable.class;
    }

    @Override
    public DataOut outTo(Appendable outTo) {

        if (schema == null) {
            return new UnknownSchemaConsumer(outTo);
        } else {
            return consumerWhenSchemaKnown(outTo, schema);
        }
    }

    protected DataOut consumerWhenSchemaKnown(Appendable appendable,
                                              DataSchema schema) {
        CSVFormat csvFormat = this.csvFormat;
        if (this.withHeader) {
            csvFormat = csvFormat.builder()
                    .setHeader(headerFrom(schema))
                    .build();
        }

        final CSVPrinter printer;
        try {
            printer = csvFormat.print(appendable);
        } catch (IOException e) {
            throw DataException.of(e);
        }

        return new KnownSchemaConsumer(printer,
                FieldValuesOut.forSchema(schema));
    }

    static class KnownSchemaConsumer implements DataOut {

        private final CSVPrinter printer;

        private final FieldValuesOut values;

        KnownSchemaConsumer(CSVPrinter printer,
                            FieldValuesOut values) {
            this.printer = printer;
            this.values = values;
        }

        @Override
        public void accept(DidoData data) {
            try {
                printer.printRecord(values.toCollection(data));
            } catch (IOException e) {
                throw DataException.of("Failed on " + data, e);
            }
        }

        @Override
        public void close() {
            try {
                printer.close();
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }
    }

    class UnknownSchemaConsumer implements DataOut {

        private final Appendable outputStream;

        private CloseableConsumer<DidoData> schemaKnownConsumer;

        UnknownSchemaConsumer(Appendable outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void accept(DidoData data) {
            if (schemaKnownConsumer == null) {
                schemaKnownConsumer = consumerWhenSchemaKnown(outputStream, data.getSchema());
            }
            schemaKnownConsumer.accept(data);
        }

        @Override
        public void close() {
            if (schemaKnownConsumer != null) {
                schemaKnownConsumer.close();
            }
        }
    }

    public static String[] headerFrom(DataSchema schema) {
        if (schema.lastIndex() < 1) {
            return new String[0];
        }
        String[] headers = new String[schema.lastIndex()];
        int column = 1;
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            while (column++ < i) {
                headers[i - 1] = "";
            }
            headers[i - 1] = Optional.ofNullable(schema.getFieldNameAt(i))
                    .map(Object::toString)
                    .orElse("");
        }
        return headers;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("CSV");
        if (this.schema != null) {
            builder.append(", with schema");
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
