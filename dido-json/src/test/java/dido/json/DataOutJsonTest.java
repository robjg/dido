package dido.json;

import com.google.gson.*;
import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import dido.how.DataOut;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

class DataOutJsonTest {

    @Test
    void testToArray() throws JSONException {

        StringBuilder result = new StringBuilder();

        try (DataOut out = DataOutJson.with()
                .outFormat(JsonDidoFormat.ARRAY)
                .withSpaceAfterSeparators(true)
                .toAppendable(result)) {

            List.of(MapData.of("Fruit", "Apple", "Qty", 5),
                            MapData.of("Fruit", "Orange", "Qty", 3))
                    .forEach(out);
        }

        String expected = "[{\"Fruit\": \"Apple\", \"Qty\": 5}, {\"Fruit\": \"Orange\", \"Qty\": 3}]";

        JSONAssert.assertEquals(expected, result.toString(), JSONCompareMode.STRICT);
    }

    record Foo(String foo) {

    }

    @Test
    void testCustomGsonSerializer() throws JSONException {

        class FooSerializer implements JsonSerializer<Foo> {

            @Override
            public JsonElement serialize(Foo src, Type typeOfSrc, JsonSerializationContext context) {

                return new JsonPrimitive(src.foo + " Foo");
            }
        }

        StringBuilder result = new StringBuilder();

        try (DataOut out = DataOutJson.with()
                .gsonBuilder(gson -> gson.registerTypeAdapter(Foo.class, new FooSerializer()))
                .strictness(Strictness.LENIENT)
                .toAppendable(result)) {

            List.of(ArrayData.of(new Foo("Some")), ArrayData.of(new Foo("Other")))
                    .forEach(out);
        }

        System.out.println(result);


        String expected = "{\"f_1\":\"Some Foo\"}{\"f_1\":\"Other Foo\"}";

        JSONAssert.assertEquals(expected, result.toString(), JSONCompareMode.LENIENT);
    }

    @Test
    void mapperFunction() throws JSONException {

        List<String> results =
                Stream.of(MapData.of("Fruit", "Apple", "Qty", 5),
                        MapData.of("Fruit", "Orange", "Qty", 3))
                        .map(DataOutJson.mapToString())
                        .toList();

        String expected1 = "{\"Fruit\": \"Apple\", \"Qty\": 5}";
        String expected2 = "{\"Fruit\": \"Orange\", \"Qty\": 3}";

        JSONAssert.assertEquals(expected1, results.get(0), JSONCompareMode.STRICT);
        JSONAssert.assertEquals(expected2, results.get(1), JSONCompareMode.STRICT);
    }
}