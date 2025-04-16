package dido.json;

import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.Writer;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class DataOutJsonWriter implements DataOutHow<Writer> {

    private final JsonWriterWrapperProvider wrapperProvider;

    final private boolean array;

    DataOutJsonWriter(JsonWriterWrapperProvider wrapperProvider, boolean array) {
        this.array = array;
        this.wrapperProvider = wrapperProvider;
    }

    @Override
    public Class<Writer> getOutType() {
        return Writer.class;
    }

    @Override
    public DataOut outTo(Writer outTo) {

        try {
            final JsonWriterWrapper writerWrapper = wrapperProvider.writerFor(outTo);

            if (array) {
                writerWrapper.getWrappedWriter().beginArray();
            }

            return new DataOut() {

                @Override
                public void close() {
                    try (JsonWriter writer = writerWrapper.getWrappedWriter()) {
                        if (array) {
                            writer.endArray();
                        }
                    } catch (IOException e) {
                        throw DataException.of(e);
                    }
                }

                @Override
                public void accept(DidoData data) {
                    try {
                        writerWrapper.write(data);
                    } catch (IOException e) {
                        throw new DataException(e);
                    }
                }
            };
        } catch (IOException e) {
            throw new DataException(e);
        }
    }

    @Override
    public String toString() {
        return "Json " + (array ? "array" : "single") + "  to OutputStream";
    }
}
