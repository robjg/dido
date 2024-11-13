package dido.json;

import dido.data.DidoData;

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


    private final Function<DidoData, String> delegate =
            DataOutJson.mapToString();

    @Override
    public String apply(DidoData data) {

        return delegate.apply(data);
    }

    @Override
    public String toString() {
        return "To Json String";
    }
}
