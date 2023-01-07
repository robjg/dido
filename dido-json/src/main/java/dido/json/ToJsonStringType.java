package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.IndexedData;
import dido.data.RepeatingData;

import java.util.function.Function;

/**
 * Provide an {@link java.util.function.Function} that writes to a JSON String.
 */
public class ToJsonStringType implements Function<IndexedData<String>, String> {

    private final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
            .registerTypeHierarchyAdapter(RepeatingData.class, new RepeatingSerializer())
            .create();

    @Override
    public String apply(IndexedData<String> stringIndexedData) {

        return gson.toJson(stringIndexedData);
    }

    @Override
    public String toString() {
        return "To Json String";
    }
}
