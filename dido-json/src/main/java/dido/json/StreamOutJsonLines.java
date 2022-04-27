package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.GenericData;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class StreamOutJsonLines implements DataOutHow<String, OutputStream> {

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut<String> outTo(OutputStream outputStream) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GenericData.class, new FieldRecordSerializer())
                .create();

        OutputStreamWriter appendable = new OutputStreamWriter(outputStream);

        return new DataOut<>() {
            @Override
            public void close() throws IOException {
                appendable.close();
            }

            @Override
            public void accept(GenericData<String> stringRecordData) {

                gson.toJson(stringRecordData, GenericData.class, appendable);
                try {
                    appendable.append('\n');
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed writing line terminator", e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "JsonLines";
    }
}
