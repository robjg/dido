package dido.json;

import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

/**
 * Wraps a Gson {@code JsonWriter}
 *
 * @see DataOutJson
 */
public interface JsonWriterWrapper extends Closeable {

    void write(DidoData data) throws IOException;

    JsonWriter getWrappedWriter();

    Writer getWriter();
}
