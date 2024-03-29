package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.*;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;


class DataSerializerTest {

    @Test
    void whenGenericDataOfPrimitivesThenCorrectJsonProduced() throws JSONException {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("type", String.class)
                .addField("foo", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        GenericData<String> data = ArrayData.valuesFor(schema)
                .of("Apple", null, 15, 26.5);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"type\":\"Apple\",\"qty\":15,\"price\":26.5}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

    @Test
    void whenGenericDataOfNestedGenericDataThenCorrectJsonProduced() throws JSONException {

        DataSchema<String> fooSchema = SchemaBuilder.forStringFields()
                .addField("foo", String.class)
                .addField("qty", int.class)
                .build();

        DataSchema<String> posSchema = SchemaBuilder.forStringFields()
                .addField("x", double.class)
                .addField("y", double.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addNestedField("foo", fooSchema)
                .addNestedField("pos", posSchema)
                .build();

        GenericData<String> data = MapData.valuesFor(schema)
                .of(MapData.valuesFor(fooSchema)
                                .of("Stuff", 15),
                        MapData.valuesFor(posSchema)
                                .of(1.2, 3.4));

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"foo\":{\"foo\":\"Stuff\",\"qty\":15},\"pos\":{\"x\":1.2,\"y\":3.4}}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

    @Test
    void whenGenericDataOfRepeatingThenCorrectJsonProduced() throws JSONException {

        DataSchema<String> posSchema = SchemaBuilder.forStringFields()
                .addField("x", double.class)
                .addField("y", double.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("foo", String.class)
                .addNestedField("positions", posSchema)
                .build();

        RepeatingData<String> positions = RepeatingData.of(
                MapData.valuesFor(posSchema).of(1.2, 3.4),
                MapData.valuesFor(posSchema).of(2.0, 3.0),
                MapData.valuesFor(posSchema).of(-7.7, -8.8));


        GenericData<String> data = MapData.valuesFor(schema)
                .of("Foo", positions);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
                .registerTypeHierarchyAdapter(RepeatingData.class, new RepeatingSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"foo\":\"Foo\",\"positions\":[{\"x\":1.2,\"y\":3.4},{\"x\":2.0,\"y\":3.0},{\"x\":-7.7,\"y\":-8.8}]}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }


}