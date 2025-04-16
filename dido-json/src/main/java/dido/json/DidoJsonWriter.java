package dido.json;

import com.google.gson.stream.JsonWriter;
import dido.data.DidoData;

import java.io.IOException;

/**
 * Something that write data to a JsonWriter.
 *
 * @see DidoJsonWriters
 * @see JsonWriterWrapper
 */
public interface DidoJsonWriter {

    JsonWriter write(DidoData data, JsonWriter jsonWriter) throws IOException;
}
