package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.util.FieldValuesOut;
import dido.how.*;
import dido.how.useful.AppendableOutHow;
import dido.how.useful.UnknownFunction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * How to write CSV Data Out.
 */
public class DataOutCsv {

    private final CSVFormat csvFormat;

    private final boolean withHeader;

    private DataOutCsv(Settings settings) {
        this.csvFormat = Objects.requireNonNullElse(settings.csvFormat, CSVFormat.DEFAULT);
        this.withHeader = settings.withHeader;
    }

    public static class Settings {

        private CSVFormat csvFormat;

        private boolean withHeader;

        public Settings csvFormat(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
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

        public UnknownHow make() {
            return new DataOutCsv(this).new UnknownHow();
        }

        public KnownHow forSchema(DataSchema schema) {
            return new DataOutCsv(this).new KnownHow(schema);
        }

        public RefinableFunction<DidoData, String> mapToString() {

            return make().mapToString();
        }
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

    public static KnownHow forSchema(DataSchema schema) {
        return with().forSchema(schema);
    }

    public static Settings with() {
        return new Settings();
    }

    public class UnknownHow extends AppendableOutHow implements RefinableOutHow<Appendable> {

        @Override
        public DataOutHow<Appendable> forSchema(DataSchema schema) {
            return new KnownHow(schema);
        }

        @Override
        public Class<Appendable> getOutType() {
            return Appendable.class;
        }

        @Override
        public DataOut outTo(Appendable outTo) {
            return new UnknownSchemaConsumer(outTo);
        }

        public RefinableFunction<DidoData, String> mapToString() {

            return UnknownFunction.of(schema -> new KnownHow(schema).mapToString());
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("CSV with no schema");
            if (withHeader) {
                builder.append(" and header");
            } else {
                builder.append(" and no header");
            }
            return builder.toString();
        }
    }

    public class KnownHow extends AppendableOutHow {

        private final DataSchema schema;

        public KnownHow(DataSchema schema) {
            this.schema = schema;
        }

        @Override
        public Class<Appendable> getOutType() {
            return Appendable.class;
        }

        @Override
        public DataOut outTo(Appendable outTo) {
            CSVFormat csvFormat = DataOutCsv.this.csvFormat;
            if (withHeader) {
                csvFormat = csvFormat.builder()
                        .setHeader(headerFrom(schema))
                        .get();
            }

            final CSVPrinter printer;
            try {
                printer = csvFormat.print(outTo);
            } catch (IOException e) {
                throw DataException.of(e);
            }

            return new KnownSchemaConsumer(printer,
                    FieldValuesOut.forSchema(schema));
        }

        public Function<DidoData, String> mapToString() {

            CSVFormat functionFormat = csvFormat.builder()
                            .setRecordSeparator("")
                            .get();

            FieldValuesOut valuesOut = FieldValuesOut.forSchema(schema);

            return data -> {
                final StringWriter out = new StringWriter();
                try (CSVPrinter csvPrinter = new CSVPrinter(out, functionFormat)) {
                    csvPrinter.printRecord(valuesOut.toCollection(data));
                    return out.toString();
                }
                catch (IOException e) {
                    throw new DataException(e);
                }
            };
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("CSV with schema");
            if (withHeader) {
                builder.append(" and header");
            } else {
                builder.append(" and no header");
            }
            return builder.toString();
        }
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

        private final Appendable outTo;

        private CloseableConsumer<DidoData> schemaKnownConsumer;

        UnknownSchemaConsumer(Appendable outTo) {
            this.outTo = outTo;
        }

        @Override
        public void accept(DidoData data) {
            if (schemaKnownConsumer == null) {
                schemaKnownConsumer = new KnownHow(data.getSchema()).outTo(outTo);
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

}
