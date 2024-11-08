package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.*;
import dido.data.util.FieldValuesIn;
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

        DidoData data = ArrayData.valuesForSchema(schema)
                .of("Apple", null, 15, 26.5);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class,
                        DataSerializer.forSchema(schema))
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

        DidoData data = MapData.valuesForSchema(schema)
                .of(MapData.valuesForSchema(fooSchema)
                                .of("Stuff", 15),
                        MapData.valuesForSchema(posSchema)
                                .of(1.2, 3.4));

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class,
                        DataSerializer.forSchema(schema))
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
                .addRepeatingNamed("positions", posSchema)
                .build();

        RepeatingData positions = RepeatingData.of(
                MapData.valuesForSchema(posSchema).of(1.2, 3.4),
                MapData.valuesForSchema(posSchema).of(2.0, 3.0),
                MapData.valuesForSchema(posSchema).of(-7.7, -8.8));


        DidoData data = MapData.valuesForSchema(schema)
                .of("Foo", positions);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class,
                        DataSerializer.forSchema(schema))
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\"foo\":\"Foo\",\"positions\":[{\"x\":1.2,\"y\":3.4},{\"x\":2.0,\"y\":3.0},{\"x\":-7.7,\"y\":-8.8}]}";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }

    @Test
    void whenNestedRefThenCorrectJsonProduced() throws JSONException {

        SchemaReference schemaReference = SchemaReference.named("Person");

        DataSchema childrenSchema = SchemaBuilder.newInstance()
                .addRepeatingNamed("People", schemaReference)
                .build();

        DataSchema personSchema = SchemaBuilder.newInstance()
                .addNamed("Name", String.class)
                .addNestedNamed("Children", childrenSchema)
                .build();

        schemaReference.set(personSchema);

        FieldValuesIn<MapData> personValue = MapData.valuesForSchema(personSchema);
        FieldValuesIn<MapData> childrenValue = MapData.valuesForSchema(childrenSchema);

        DidoData data = personValue.of("Alice", childrenValue.of(RepeatingData.of(
                        personValue.of("Bob", childrenValue.of(
                                RepeatingData.of(
                                        personValue.of("Cathrine", null),
                                        personValue.of("Diana", null)))),
                        personValue.of("Eric", null),
                        personValue.of("Fred", childrenValue.of(
                                RepeatingData.of(
                                        personValue.of("Greg", null))))
                )));

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(IndexedData.class,
                        DataSerializer.forSchema(personSchema))
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(data, IndexedData.class);

        String expected = "{\n" +
                "  \"Name\": \"Alice\",\n" +
                "  \"Children\": {\n" +
                "    \"People\": [\n" +
                "      {\n" +
                "        \"Name\": \"Bob\",\n" +
                "        \"Children\": {\n" +
                "          \"People\": [\n" +
                "            {\n" +
                "              \"Name\": \"Cathrine\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"Name\": \"Diana\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"Name\": \"Eric\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"Name\": \"Fred\",\n" +
                "        \"Children\": {\n" +
                "          \"People\": [\n" +
                "            {\n" +
                "              \"Name\": \"Greg\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n";

        JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT);
    }
}