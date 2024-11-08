package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private final Gson gson;

    final private boolean array;

    private DataOutJsonWriter(boolean array) {
        this(new GsonBuilder()
                .registerTypeHierarchyAdapter(DidoData.class,
                        DataSerializer.forUnknownSchema())
                .create(), array);
    }

    DataOutJsonWriter(Gson gson, boolean array) {
        this.array = array;
        this.gson = gson;
    }

    public static DataOutHow<Writer> streamOutSingle() {
        return new DataOutJsonWriter(false);
    }

    public static DataOutHow<Writer> streamOutArray() {
        return new DataOutJsonWriter(true);
    }


    @Override
    public Class<Writer> getOutType() {
        return Writer.class;
    }

    @Override
    public DataOut outTo(Writer outTo) {

        try {
            final JsonWriter jsonWriter = gson.newJsonWriter(outTo);

            if (array) {
                jsonWriter.beginArray();
            }

            return new DataOut() {

                @Override
                public void close() {
                    try (jsonWriter) {
                        if (array) {
                            jsonWriter.endArray();
                        }
                    } catch (IOException e) {
                        throw DataException.of(e);
                    }
                }

                @Override
                public void accept(DidoData data) {
                    gson.toJson(data, DidoData.class, jsonWriter);
                }
            };
        } catch (IOException e) {
            throw DataException.of(e);
        }
    }

    @Override
    public String toString() {
        return "Json " + (array ? "array" : "single") + "  to OutputStream";
    }
}
