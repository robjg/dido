package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class StreamOutJson implements DataOutHow<String, OutputStream> {

    private final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
            .create();

    final private boolean array;

    private StreamOutJson(boolean array) {
        this.array = array;
    }

    public static DataOutHow<String, OutputStream> streamOutSingle() {
        return new StreamOutJson(false);
    }

    public static DataOutHow<String, OutputStream> streamOutArray() {
        return new StreamOutJson(true);
    }


    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<String> outTo(OutputStream outputStream) throws IOException {

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        if (array) {
            writer.beginArray();
        }

        return new DataOut<>() {
            @Override
            public void close() throws IOException {
                writer.endArray();
                writer.close();
            }

            @Override
            public void accept(GenericData<String> stringRecordData) {
                gson.toJson(stringRecordData, IndexedData.class, writer);

            }
        };
    }

    @Override
    public String toString() {
        return "Json " + (array ? "array" : "single") + "  to OutputStream";
    }
}
