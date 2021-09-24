package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dido.data.GenericData;
import dido.pickles.DataOut;
import dido.pickles.StreamOut;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class StreamOutJson implements StreamOut<String> {

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<String> outTo(OutputStream outputStream) throws IOException {

        Gson gson = new GsonBuilder().registerTypeAdapter(GenericData.class, new FieldRecordSerializer())
                .create();

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writer.beginArray();

        return new DataOut<String>() {
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
