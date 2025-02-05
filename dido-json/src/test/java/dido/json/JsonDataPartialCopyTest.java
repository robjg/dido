package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import dido.data.*;
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

        String json = "{ 'String': ['foo', 'bar'], " +
                "'Boolean': [true, true], " +
                "'Double': [22.5, 44.3] }";

        DidoData data = DataInJson.mapFromString().apply(json);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("String", String[].class)
                .addNamed("Boolean", boolean[].class)
                .addNamed("Double", double[].class)
                .build();

        assertThat(data.getSchema(), is(expectedSchema));
        assertThat(data.getAt(1), is(new String[] { "foo", "bar" }));
        assertThat(data.getAt(2), is(new boolean[] { true, true }));
        assertThat(data.getAt(3), is(new double[] { 22.5, 44.3 }));
    }

    @Test
    void withNestedRepeatingField() {

        String json = "{ \"OrderId\": \"A123\",\n" +
                "  \"OrderLines\": [\n" +
                "  { \"Fruit\": \"Apple\", \"Qty\": 5 },\n" +
                "  { \"Fruit\": \"Pear\", \"Qty\": 4 }\n" +
                "\t]\n" +
                "\t}\n";

        DataFactoryProvider dataFactoryProvider = new ArrayDataDataFactoryProvider();

        Gson gson = JsonDataPartialCopy.registerPartialSchema(
                        new GsonBuilder(),
                        SchemaBuilder.newInstance()
                                .addRepeatingNamed("OrderLines", DataSchema.emptySchema())
                                .build(), dataFactoryProvider)
                .create();

        DidoData result = gson.fromJson(json, DidoData.class);

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Apple", 5.0),
                                ArrayData.valuesWithSchema(nestedSchema)
                                        .of("Pear", 4.0)));

        DataSchema resultSchema = result.getSchema();

        assertThat(resultSchema, is(expectedSchema));

        assertThat(result, is(expectedData));
    }
}