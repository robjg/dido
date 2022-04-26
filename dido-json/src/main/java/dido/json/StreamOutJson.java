package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dido.data.GenericData;
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

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<String> outTo(OutputStream outputStream) throws IOException {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GenericData.class, new FieldRecordSerializer())
                .create();

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writer.beginArray();

        return new DataOut<>() {
            @Override
            public void close() throws IOException {
                writer.endArray();
                writer.close();
            }

            @Override
            public void accept(GenericData<String> stringRecordData) {
                gson.toJson(stringRecordData, GenericData.class, writer);

            }
        };
    }

}
