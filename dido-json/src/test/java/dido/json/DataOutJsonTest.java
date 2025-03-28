package dido.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dido.data.ArrayData;
import dido.data.MapData;
import dido.how.DataOut;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
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

    @Test
    void testCustomGsonSerializer() throws JSONException {

        class FooSerializer implements JsonSerializer<String> {

            @Override
            public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {

                return new JsonPrimitive(src + " Foo");
            }
        }

        StringBuilder result = new StringBuilder();

        try (DataOut out = DataOutJson.with()
                .gsonBuilder(gson -> gson.registerTypeAdapter(String.class, new FooSerializer()))
                .toAppendable(result)) {

            List.of(ArrayData.of("Some"), ArrayData.of("Other"))
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
                        .collect(Collectors.toList());

        String expected1 = "{\"Fruit\": \"Apple\", \"Qty\": 5}";
        String expected2 = "{\"Fruit\": \"Orange\", \"Qty\": 3}";

        JSONAssert.assertEquals(expected1, results.get(0), JSONCompareMode.STRICT);
        JSONAssert.assertEquals(expected2, results.get(1), JSONCompareMode.STRICT);
    }
}