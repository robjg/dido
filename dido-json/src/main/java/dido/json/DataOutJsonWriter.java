package dido.json;

import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;

import java.io.IOException;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class DataOutJsonWriter implements DataOut {

    private final JsonWriterWrapper writerWrapper;

    final private boolean array;

    public DataOutJsonWriter(JsonWriterWrapper writerWrapper, boolean array) throws IOException {
        this.array = array;
        this.writerWrapper = writerWrapper;
        if (array) {
            writerWrapper.getWrappedWriter().beginArray();
        }
    }

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

    @Override
    public String toString() {
        return "Json " + (array ? "array" : "single") + "  to OutputStream";
    }
}
