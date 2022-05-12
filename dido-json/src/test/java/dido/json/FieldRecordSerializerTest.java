package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.*;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;


class FieldRecordSerializerTest {

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
                .registerTypeHierarchyAdapter(IndexedData.class, new FieldRecordSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"type\":\"Apple\",\"qty\":15,\"price\":26.5}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

    @Disabled("TODO: Implement nested JSON.")
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
                .of("foo", MapData.valuesFor(fooSchema)
                                .of("foo", "Stuff", "qty", 15),
                        "pos", MapData.valuesFor(posSchema)
                                .of("x", 1.2, "y", 3.4));

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IndexedData.class, new FieldRecordSerializer())
                .create();

        String json = gson.toJson(data, IndexedData.class);

        System.out.println(json);

        String expected = "{\"type\":\"Apple\",\"qty\":15,\"price\":26.5}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

}