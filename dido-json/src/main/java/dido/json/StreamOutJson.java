package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dido.data.GenericData;
import org.oddjob.dido.CloseableConsumer;
import org.oddjob.dido.StreamOut;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class StreamOutJson implements StreamOut<String> {

    @Override
    public CloseableConsumer<GenericData<String>> consumerFor(OutputStream outputStream) throws IOException {

        Gson gson = new GsonBuilder().registerTypeAdapter(GenericData.class, new FieldRecordSerializer())
                .create();

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writer.beginArray();

        return new CloseableConsumer<GenericData<String>>() {
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
