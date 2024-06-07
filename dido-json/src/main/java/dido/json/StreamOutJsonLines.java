package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DidoData;
import dido.data.IndexedData;
import dido.data.RepeatingData;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Provide an {@link DataOut} that writes an array of JSON records.
 */
public class StreamOutJsonLines implements DataOutHow<OutputStream> {

    @Override
    public Class<OutputStream> getOutType() {
        return OutputStream.class;
    }

    @Override
    public DataOut outTo(OutputStream outputStream) {

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
                .registerTypeHierarchyAdapter(RepeatingData.class, new RepeatingSerializer())
                .serializeSpecialFloatingPointValues()
                .create();

        OutputStreamWriter appendable = new OutputStreamWriter(outputStream);

        return new DataOut() {
            @Override
            public void close() throws IOException {
                appendable.close();
            }

            @Override
            public void accept(DidoData stringRecordData) {

                gson.toJson(stringRecordData, DidoData.class, appendable);
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
