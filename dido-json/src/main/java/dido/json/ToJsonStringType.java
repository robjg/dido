package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DidoData;
import dido.data.IndexedData;
import dido.data.RepeatingData;

import java.util.function.Function;

/**
 *
 * @oddjob.description Provides a Mapping Function that will convert a Dido Data
 * into a JSON String.
 *
 * @oddjob.example From JSON Strings using a Mapping function and back again.
 * {@oddjob.xml.resource dido/json/FromJsonMapExample.xml}
 *
 */
public class ToJsonStringType implements Function<DidoData, String> {

    private final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
            .registerTypeHierarchyAdapter(RepeatingData.class, new RepeatingSerializer())
            .create();

    @Override
    public String apply(DidoData stringIndexedData) {

        return gson.toJson(stringIndexedData);
    }

    @Override
    public String toString() {
        return "To Json String";
    }
}
