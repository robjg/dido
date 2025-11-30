package dido.json;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class GsonAssumptionsTest {

    @Test
    void stringAssumptions() {

        Gson gson = new Gson();

        JsonElement element = new JsonStreamParser("'Foo'").next();

        assertThat(element.isJsonPrimitive(), is(true));

        Object result = gson.fromJson(element, Object.class);

        assertThat(result, is("Foo"));
    }

    @Test
    void numberAssumptions() {

        Gson gson = new GsonBuilder()
                .create();

        JsonPrimitive longElement = (JsonPrimitive) new JsonStreamParser("5").next();

        assertThat(longElement.isNumber(), is(true));

        Object longAsObject = gson.fromJson(longElement, Object.class);

        assertThat(longAsObject, instanceOf(Double.class));

        assertThat(longAsObject, is(5.0));

        Number longAsNumber = gson.fromJson(longElement, Number.class);

        assertThat(longAsNumber, instanceOf(LazilyParsedNumber.class));

        assertThat(longAsNumber, is(new LazilyParsedNumber("5")));

        JsonPrimitive doubleElement = (JsonPrimitive) new JsonStreamParser("4.2").next();

        assertThat(doubleElement.isNumber(), is(true));

        Object doubleAsObject = gson.fromJson(doubleElement, Object.class);

        assertThat(doubleAsObject, instanceOf(Double.class));

        assertThat(doubleAsObject, is(4.2));

        Number doubleAsNumber = gson.fromJson(doubleElement, Number.class);

        assertThat(doubleAsNumber, instanceOf(LazilyParsedNumber.class));

        assertThat(doubleAsNumber, is(new LazilyParsedNumber("4.2")));
    }

    @Test
    void numberAssumptionsNumberLongOrDouble() {

        Gson gson = new GsonBuilder()
                .setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();

        JsonPrimitive longElement = (JsonPrimitive) new JsonStreamParser("5").next();

        assertThat(longElement.isNumber(), is(true));

        Object longAsObject = gson.fromJson(longElement, Object.class);

        assertThat(longAsObject, instanceOf(Double.class));

        assertThat(longAsObject, is(5.0));

        Number longAsNumber = gson.fromJson(longElement, Number.class);

        assertThat(longAsNumber, instanceOf(Long.class));

        assertThat(longAsNumber, is(5L));

        JsonPrimitive doubleElement = (JsonPrimitive) new JsonStreamParser("4.2").next();

        assertThat(doubleElement.isNumber(), is(true));

        Object doubleAsObject = gson.fromJson(doubleElement, Object.class);

        assertThat(doubleAsObject, instanceOf(Double.class));

        assertThat(doubleAsObject, is(4.2));

        Number doubleAsNumber = gson.fromJson(doubleElement, Number.class);

        assertThat(doubleAsNumber, instanceOf(Double.class));

        assertThat(doubleAsNumber, is(4.2));
    }

    @Test
    void numberAssumptionsObjectLongOrDouble() {

        Gson gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();

        JsonPrimitive longElement = (JsonPrimitive) new JsonStreamParser("5").next();

        assertThat(longElement.isNumber(), is(true));

        Object longAsObject = gson.fromJson(longElement, Object.class);

        assertThat(longAsObject, instanceOf(Long.class));

        assertThat(longAsObject, is(5L));

        Number longAsNumber = gson.fromJson(longElement, Number.class);

        assertThat(longAsNumber, instanceOf(LazilyParsedNumber.class));

        assertThat(longAsNumber, is(new LazilyParsedNumber("5")));

        JsonPrimitive doubleElement = (JsonPrimitive) new JsonStreamParser("4.2").next();

        assertThat(doubleElement.isNumber(), is(true));

        Object doubleAsObject = gson.fromJson(doubleElement, Object.class);

        assertThat(doubleAsObject, instanceOf(Double.class));

        assertThat(doubleAsObject, is(4.2));

        Number doubleAsNumber = gson.fromJson(doubleElement, Number.class);

        assertThat(doubleAsNumber, instanceOf(LazilyParsedNumber.class));

        assertThat(doubleAsNumber, is(new LazilyParsedNumber("4.2")));
    }

    @Test
    void longArrays() {

        String json = "[1,2,3,4]";

        Gson gson = new GsonBuilder().create();

        assertThat(gson.fromJson(json, double[].class), is(new double[]{1.0, 2.0, 3.0, 4.0}));
        assertThat(gson.fromJson(json, int[].class), is(new int[]{1, 2, 3, 4}));
        assertThat(gson.fromJson(json, long[].class), is(new long[]{1L, 2L, 3L, 4L}));

        assertThat(gson.fromJson(json, Object.class), is(List.of(1.0, 2.0, 3.0, 4.0)));

        assertThat(gson.fromJson(json, Object[].class), is( new Object[] { 1.0, 2.0, 3.0, 4.0 }));
    }

    @Test
    void longArraysNumberLongOrDouble() {

        String json = "[1,2,3,4]";

        Gson gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();

        assertThat(gson.fromJson(json, double[].class), is(new double[]{1.0, 2.0, 3.0, 4.0}));
        assertThat(gson.fromJson(json, int[].class), is(new int[]{1, 2, 3, 4}));
        assertThat(gson.fromJson(json, long[].class), is(new long[]{1L, 2L, 3L, 4L}));

        assertThat(gson.fromJson(json, Object.class), is(List.of(1L, 2L, 3L, 4L)));

        assertThat(gson.fromJson(json, Object[].class), is( new Object[] { 1L, 2L, 3L, 4L }));
    }

    @Test
    void mapArrays() {

        String json = "{ foo: { longArray: [1,2,3,4], someLong: 5 } }";

        Gson gson1 = new GsonBuilder()
                .create();

        assertThat(gson1.fromJson(json, Object.class),
                is(Map.of("foo",
                        Map.of("longArray", List.of(1.0, 2.0, 3.0, 4.0),
                                "someLong", 5.0))));

        Gson gson2 = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create();

        assertThat(gson2.fromJson(json, Object.class),
                is(Map.of("foo",
                        Map.of("longArray", List.of(1L, 2L, 3L, 4L),
                                "someLong", 5L))));
    }
}
