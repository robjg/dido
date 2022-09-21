package dido.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JsonSchemaExtractorTest {

    static class NestedThing {

        String nestedString = "Nested";
    }

    static class AllFields {

        String aString = "Apple";

        boolean aBoolean = true;

        byte aByte = Byte.MAX_VALUE;

        short aShort = Short.MAX_VALUE;

        int anInt = Integer.MAX_VALUE;

        long aLong = Long.MAX_VALUE;

        float aFloat = 1.234F;

        double aDouble = 123456.78;

        Number aNumber = 67.2;

        int[] anArray = { 1 , 2, 3 };

        Date aDate = new Date();

        NestedThing aNested = new NestedThing();
    }

    public static class TestDeserializer implements JsonDeserializer<DataSchema<String>> {

        private final JsonSchemaExtractor test;

        public TestDeserializer(DataSchema<String> schema) {
            this.test = JsonSchemaExtractor.withPartialSchema(schema);
        }

        @Override
        public DataSchema<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) json;

            return test.fromElement(jsonObject);
        }
    }

    @Test
    void testWithAllFields() {

        Gson gson = JsonSchemaExtractor.registerNoSchema(new GsonBuilder())
                .create();

        String json = gson.toJson(new AllFields());

        DataSchema<String> result = gson.fromJson(json,
                new TypeToken<DataSchema<String>>() {}.getType());

        assertThat(result.getType("aString"), is(String.class));
        assertThat(result.getType("aBoolean"), is(boolean.class));
        assertThat(result.getType("aByte"), is(double.class));
        assertThat(result.getType("aShort"), is(double.class));
        assertThat(result.getType("anInt"), is(double.class));
        assertThat(result.getType("aLong"), is(double.class));
        assertThat(result.getType("aFloat"), is(double.class));
        assertThat(result.getType("aDouble"), is(double.class));
        assertThat(result.getType("aNumber"), is(double.class));
        assertThat(result.getType("anArray"), is(double[].class));
        assertThat(result.getType("aDate"), is(String.class));

        assertThat(result.getSchemaField("aNested").isNested(), is(false));
        assertThat(result.getType("aNested"), is(Map.class));

        assertThat(result.firstIndex(), is(1));
        assertThat(result.lastIndex(), is(12));
    }

    @Test
    void testWithNestField() {

        JsonObject line1 = new JsonObject();
        line1.addProperty("Fruit", "Apple");
        line1.addProperty("Qty", 5);

        JsonObject line2 = new JsonObject();
        line2.addProperty("Fruit", "Pear");
        line2.addProperty("Qty", 4);

        JsonArray orderLines = new JsonArray(2);
        orderLines.add(line1);
        orderLines.add(line2);

        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("OrderId", "A123");
        rootObject.add("OrderLines", orderLines);

        DataSchema<String> partialSchema = SchemaBuilder.forStringFields()
                .addRepeatingField("OrderLines", DataSchema.emptySchema())
                .build();

        JsonSchemaExtractor test = JsonSchemaExtractor.withPartialSchema(partialSchema);

        DataSchema<String> schema = test.fromElement(rootObject);

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Qty", double.class)
                .build();

        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", nestedSchema)
                .build();

        assertThat(schema, is(expectedSchema));
    }
}