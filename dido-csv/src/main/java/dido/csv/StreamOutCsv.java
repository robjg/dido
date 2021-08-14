package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.oddjob.dido.CloseableConsumer;
import org.oddjob.dido.StreamOut;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Optional;

public class StreamOutCsv<F> implements StreamOut<F> {

    private final CSVFormat csvFormat;

    private final DataSchema<F> schema;

    private final boolean withHeaders;

    public StreamOutCsv(boolean withHeaders) {
        this(null, null, withHeaders);
    }

    public StreamOutCsv(DataSchema<F> schema, boolean withHeaders) {
        this(null, schema, withHeaders);
    }

    public StreamOutCsv(CSVFormat csvFormat, DataSchema<F> schema, boolean withHeaders) {
        this.csvFormat = csvFormat == null ? CSVFormat.DEFAULT : csvFormat;
        this.schema = schema;
        this.withHeaders = withHeaders;
    }

    @Override
    public CloseableConsumer<GenericData<F>> consumerFor(OutputStream outputStream) throws IOException {

        if (schema == null) {
            return new UnknownSchemaConsumer(outputStream);
        }
        else {
            return consumerWhenSchemaKnown(outputStream, schema);
        }
    }

    protected CloseableConsumer<GenericData<F>> consumerWhenSchemaKnown(OutputStream outputStream,
                                                                             DataSchema<F> schema) throws IOException {
        CSVFormat csvFormat = this.csvFormat;
        if (this.withHeaders) {
            csvFormat = csvFormat.withHeader(headers(schema));
        }

        Writer writer = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = csvFormat.print(writer);

        return new KnownSchemaConsumer(printer);
    }

    static class KnownSchemaConsumer<F> implements CloseableConsumer<GenericData<F>> {

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

    class UnknownSchemaConsumer implements CloseableConsumer<GenericData<F>> {

        private final OutputStream outputStream;

        private CloseableConsumer<GenericData<F>> schemaKnownConsumer;

        UnknownSchemaConsumer(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void accept(GenericData<F> data) {
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
        public void close() throws IOException {
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
                headers[i -1] = "";
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
            values[i - 1] = data.getObjectAt(i);
        }
        return values;
    }
}
