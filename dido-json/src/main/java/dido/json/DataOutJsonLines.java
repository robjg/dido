package dido.json;

import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.Writer;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class DataOutJsonLines implements DataOutHow<Writer> {

    private final JsonWriterWrapperProvider writerProvider;

    private final String lineSeparator;

    DataOutJsonLines(JsonWriterWrapperProvider writerProvider,
                     String lineSeparator) {
        this.writerProvider = writerProvider;
        this.lineSeparator = lineSeparator;
    }

    @Override
    public Class<Writer> getOutType() {
        return Writer.class;
    }

    @Override
    public DataOut outTo(Writer writer) {

            return new DataOut() {
                @Override
                public void close() {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        throw DataException.of(e);
                    }
                }

                @Override
                public void accept(DidoData data) {

                    try {
                        JsonWriterWrapper jsonWriter = writerProvider.writerFor(writer);
                        jsonWriter.write(data);
                        jsonWriter.getWrappedWriter().flush();
                        writer.append(lineSeparator);
                        writer.flush();
                    } catch (IOException e) {
                        throw DataException.of("Failed writing line terminator", e);
                    }
                }
            };
    }

    @Override
    public String toString() {
        return "JsonLines";
    }
}
