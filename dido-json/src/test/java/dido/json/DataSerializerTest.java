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
    void whenDataOfPrimitivesThenCorrectJsonProduced() throws JSONException {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("foo", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoData data = ArrayData.valuesFor(schema)
                .of("Apple", null, 15, 26.5);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class, new DataSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"type\":\"Apple\",\"qty\":15,\"price\":26.5}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

    @Test
    void whenDataOfNestedGenericDataThenCorrectJsonProduced() throws JSONException {

        DataSchema fooSchema = SchemaBuilder.newInstance()
                .addNamed("foo", String.class)
                .addNamed("qty", int.class)
                .build();

        DataSchema posSchema = SchemaBuilder.newInstance()
                .addNamed("x", double.class)
                .addNamed("y", double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNestedNamed("foo", fooSchema)
                .addNestedNamed("pos", posSchema)
                .build();

        DidoData data = MapData.valuesFor(schema)
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
    void whenDataOfRepeatingThenCorrectJsonProduced() throws JSONException {

        DataSchema posSchema = SchemaBuilder.newInstance()
                .addNamed("x", double.class)
                .addNamed("y", double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("foo", String.class)
                .addNestedNamed("positions", posSchema)
                .build();

        RepeatingData positions = RepeatingData.of(
                MapData.valuesFor(posSchema).of(1.2, 3.4),
                MapData.valuesFor(posSchema).of(2.0, 3.0),
                MapData.valuesFor(posSchema).of(-7.7, -8.8));


        DidoData data = MapData.valuesFor(schema)
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