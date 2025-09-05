package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JsonDataPartialCopyTest {

    @Test
    void assumptions() {

        Gson gson = new Gson();

        JsonElement element = new JsonStreamParser("'Foo'").next();

        Object result = gson.fromJson(element, Object.class);

        assertThat(result, is("Foo"));
    }

    @Test
    void primitives() {

        String json = "{ 'String': 'foo', 'Boolean': true, 'Double': 22.5 }";

        DidoData data = DataInJson.mapFromString().apply(json);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("String", String.class)
                .addNamed("Boolean", boolean.class)
                .addNamed("Double", double.class)
                .build();

        assertThat(data.getSchema(), is(expectedSchema));
        assertThat(data, is(DidoData.of("foo", true, 22.5)));
    }

    @Test
    void objects() {

        String json = "{ 'Point': { 'x': 10, 'y': 5 } }";

        DidoData data = DataInJson.mapFromString().apply(json);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Point", Map.class)
                .build();

        assertThat(data.getSchema(), is(expectedSchema));
        assertThat(data.getNamed("Point"), is(Map.of("x", 10.0, "y", 5.0)));
    }

    @Test
    void arrays() {

        String json = "{ " +
                "'String': ['foo', 'bar'], " +
                "'Boolean': [true, true], " +
                "'Double': [22.5, 44.3]," +
                "'Mixed': ['foo', 22.5, true]," +
                "'Nested': [[1,2], [3,4]]," +
                "'Object': [{ 'x': 10, 'y': 5 }, { 'x': 2, 'y': 3 }]" +
                " }";

        DidoData data = DataInJson.mapFromString().apply(json);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("String", String[].class)
                .addNamed("Boolean", boolean[].class)
                .addNamed("Double", double[].class)
                .addNamed("Mixed", Object[].class)
                .addNamed("Nested", Object[].class)
                .addNamed("Object", Object[].class)
                .build();

        assertThat(data.getSchema(), is(expectedSchema));
        assertThat(data.getAt(1), is(new String[]{"foo", "bar"}));
        assertThat(data.getAt(2), is(new boolean[]{true, true}));
        assertThat(data.getAt(3), is(new double[]{22.5, 44.3}));
        assertThat(data.getAt(4), is(new Object[]{"foo", 22.5, true}));
        assertThat(data.getAt(5), is(new Object[]{new Object[]{1.0, 2.0}, new Object[]{3.0, 4.0}}));
        assertThat(data.getAt(6), is(new Object[]{
                Map.of("x", 10.0, "y", 5.0),
                Map.of("x", 2.0, "y", 3.0)}));
    }

    @Test
    void withNestedRepeatingField() {

        String json = "{ \"OrderId\": \"A123\",\n" +
                "  \"OrderLines\": [\n" +
                "  { \"Fruit\": \"Apple\", \"Qty\": 5 },\n" +
                "  { \"Fruit\": \"Pear\", \"Qty\": 4 }\n" +
                "\t]\n" +
                "\t}\n";

        Gson gson = JsonDataPartialCopy.registerPartialSchema(
                        new GsonBuilder(),
                        SchemaBuilder.newInstance()
                                .addRepeatingNamed("OrderLines", DataSchema.emptySchema())
                                .build())
                .create();

        DidoData result = gson.fromJson(json, DidoData.class);

        DataSchema expectedNestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", expectedNestedSchema)
                .build();

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("A123",
                        RepeatingData.of(ArrayData.withSchema(expectedNestedSchema)
                                        .of("Apple", 5.0),
                                ArrayData.withSchema(expectedNestedSchema)
                                        .of("Pear", 4.0)));

        DataSchema resultSchema = result.getSchema();

        assertThat(resultSchema, is(expectedSchema));

        assertThat(result, is(expectedData));
    }
}