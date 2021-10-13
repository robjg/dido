package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.how.CloseableConsumer;
import dido.how.DataOut;
import dido.how.DataOutHow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Optional;

public class StreamOutCsv implements DataOutHow<String, OutputStream> {

    private final CSVFormat csvFormat;

    private final DataSchema<String> schema;

    private final boolean withHeaders;

    public StreamOutCsv(boolean withHeaders) {
        this(null, null, withHeaders);
    }

    public StreamOutCsv(DataSchema<String> schema, boolean withHeaders) {
        this(null, schema, withHeaders);
    }

    public StreamOutCsv(CSVFormat csvFormat, DataSchema<String> schema, boolean withHeaders) {
        this.csvFormat = csvFormat == null ? CSVFormat.DEFAULT : csvFormat;
        this.schema = schema;
        this.withHeaders = withHeaders;
    }

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<String> outTo(OutputStream outputStream) throws IOException{

        if (schema == null) {
            return new UnknownSchemaConsumer(outputStream);
        } else {
            return consumerWhenSchemaKnown(outputStream, schema);
        }
    }

    protected DataOut<String> consumerWhenSchemaKnown(OutputStream outputStream,
                                                 DataSchema<String> schema) throws IOException{
        CSVFormat csvFormat = this.csvFormat;
        if (this.withHeaders) {
            csvFormat = csvFormat.withHeader(headers(schema));
        }

        Writer writer = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = csvFormat.print(writer);

        return new KnownSchemaConsumer<>(printer);
    }

    static class KnownSchemaConsumer<F> implements DataOut<F> {

        final CSVPrinter printer;

        KnownSchemaConsumer(CSVPrinter printer) {
            this.printer = printer;
        }

        @Override
        public void accept(GenericData<F> data) {
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

    class UnknownSchemaConsumer implements DataOut<String> {

        private final OutputStream outputStream;

        private CloseableConsumer<GenericData<String>> schemaKnownConsumer;

        UnknownSchemaConsumer(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void accept(GenericData<String> data) {
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

    public static String[] headers(DataSchema<?> schema) {
        if (schema.lastIndex() < 1) {
            return new String[0];
        }
        String[] headers = new String[schema.lastIndex()];
        int column = 1;
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            while (column++ < i) {
                headers[i - 1] = "";
            }
            headers[i - 1] = Optional.ofNullable(schema.getFieldAt(i))
                    .map(Object::toString)
                    .orElse("");
        }
        return headers;
    }

    public static Object[] toValues(IndexedData<?> data) {
        DataSchema<?> schema = data.getSchema();
        if (schema.lastIndex() < 1) {
            return new Object[0];
        }
        Object[] values = new Object[schema.lastIndex()];
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            values[i - 1] = data.getAt(i);
        }
        return values;
    }
}
