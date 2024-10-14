package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.IndexedData;
import dido.data.IndexedSchema;
import dido.how.CloseableConsumer;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;
import java.util.Optional;

/**
 * How to write CSV Data Out.
 */
public class CsvDataOutHow implements DataOutHow<OutputStream> {

    private final CSVFormat csvFormat;

    private final DataSchema schema;

    private final boolean withHeader;

    public static class Options {

        private CSVFormat csvFormat;

        private DataSchema schema;

        private boolean withHeader;

        public Options csvFormat(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
            return this;
        }

        public Options schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Options withHeader(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

        public DataOutHow<OutputStream> make() {
            return new CsvDataOutHow(this);
        }
    }

    private CsvDataOutHow(Options options) {
        this.csvFormat = Objects.requireNonNullElse(options.csvFormat, CSVFormat.DEFAULT);
        this.schema = options.schema;
        this.withHeader = options.withHeader;
    }

    public static Options with() {
        return new Options();
    }

    public static DataOutHow<OutputStream> withDefaultOptions() {
        return new Options().make();
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut outTo(OutputStream outputStream) throws IOException {

        if (schema == null) {
            return new UnknownSchemaConsumer(outputStream);
        } else {
            return consumerWhenSchemaKnown(outputStream, schema);
        }
    }

    protected DataOut consumerWhenSchemaKnown(OutputStream outputStream,
                                                      DataSchema schema) throws IOException {
        CSVFormat csvFormat = this.csvFormat;
        if (this.withHeader) {
            csvFormat = csvFormat.withHeader(headerFrom(schema));
        }

        Writer writer = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = csvFormat.print(writer);

        return new KnownSchemaConsumer(printer);
    }

    static class KnownSchemaConsumer implements DataOut {

        private final CSVPrinter printer;

        KnownSchemaConsumer(CSVPrinter printer) {
            this.printer = printer;
        }

        @Override
        public void accept(DidoData data) {
            try {
                printer.printRecord(toValues(data));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed on " + data, e);
            }
        }

        @Override
        public void close() throws IOException {
            printer.close();
        }
    }

    class UnknownSchemaConsumer implements DataOut {

        private final OutputStream outputStream;

        private CloseableConsumer<DidoData> schemaKnownConsumer;

        UnknownSchemaConsumer(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void accept(DidoData data) {
            if (schemaKnownConsumer == null) {
                try {
                    schemaKnownConsumer = consumerWhenSchemaKnown(outputStream, data.getSchema());
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed on " + data, e);
                }
            }
            schemaKnownConsumer.accept(data);
        }

        @Override
        public void close() throws Exception {
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

    public static Object[] toValues(IndexedData data) {
        IndexedSchema schema = data.getSchema();
        if (schema.lastIndex() < 1) {
            return new Object[0];
        }
        Object[] values = new Object[schema.lastIndex()];
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            values[i - 1] = data.getAt(i);
        }
        return values;
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
