package dido.csv;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.IndexedData;
import dido.data.IndexedSchema;
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

/**
 * How to write CSV Data Out.
 */
public class DataOutCsv implements DataOutHow<OutputStream> {

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

        public DataOutHow<OutputStream> make() {
            return new DataOutCsv(this);
        }
    }

    private DataOutCsv(Settings settings) {
        this.csvFormat = Objects.requireNonNullElse(settings.csvFormat, CSVFormat.DEFAULT);
        this.schema = settings.schema;
        this.withHeader = settings.withHeader;
    }

    public static DataOut toOutputStream(OutputStream outputStream) {

        return withDefaults().outTo(outputStream);
    }

    public static DataOut toPath(Path path) {

        try {
            return toOutputStream(Files.newOutputStream(path));
        } catch (IOException e) {
            throw DataException.of(e);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static DataOutHow<OutputStream> withDefaults() {
        return new Settings().make();
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut outTo(OutputStream outTo) {

        if (schema == null) {
            return new UnknownSchemaConsumer(outTo);
        } else {
            return consumerWhenSchemaKnown(outTo, schema);
        }
    }

    protected DataOut consumerWhenSchemaKnown(OutputStream outputStream,
                                                      DataSchema schema) {
        CSVFormat csvFormat = this.csvFormat;
        if (this.withHeader) {
            csvFormat = csvFormat.withHeader(headerFrom(schema));
        }

        Writer writer = new OutputStreamWriter(outputStream);
        final CSVPrinter printer;
        try {
            printer = csvFormat.print(writer);
        } catch (IOException e) {
            throw DataException.of(e);
        }

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

        private final OutputStream outputStream;

        private CloseableConsumer<DidoData> schemaKnownConsumer;

        UnknownSchemaConsumer(OutputStream outputStream) {
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
