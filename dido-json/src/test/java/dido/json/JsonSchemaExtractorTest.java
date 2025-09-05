package dido.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dido.data.DataSchema;
import dido.data.schema.SchemaBuilder;
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

    public static class TestDeserializer implements JsonDeserializer<DataSchema> {

        private final JsonSchemaExtractor test;

        public TestDeserializer(DataSchema schema) {
            this.test = JsonSchemaExtractor.withPartialSchema(schema);
        }

        @Override
        public DataSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) json;

            return test.fromElement(jsonObject);
        }
    }

    @Test
    void testWithAllFields() {

        Gson gson = JsonSchemaExtractor.registerNoSchema(new GsonBuilder())
                .create();

        String json = gson.toJson(new AllFields());

        DataSchema result = gson.fromJson(json,
                new TypeToken<DataSchema>() {}.getType());

        assertThat(result.getTypeNamed("aString"), is(String.class));
        assertThat(result.getTypeNamed("aBoolean"), is(boolean.class));
        assertThat(result.getTypeNamed("aByte"), is(double.class));
        assertThat(result.getTypeNamed("aShort"), is(double.class));
        assertThat(result.getTypeNamed("anInt"), is(double.class));
        assertThat(result.getTypeNamed("aLong"), is(double.class));
        assertThat(result.getTypeNamed("aFloat"), is(double.class));
        assertThat(result.getTypeNamed("aDouble"), is(double.class));
        assertThat(result.getTypeNamed("aNumber"), is(double.class));
        assertThat(result.getTypeNamed("anArray"), is(double[].class));
        assertThat(result.getTypeNamed("aDate"), is(String.class));

        assertThat(result.getSchemaFieldNamed("aNested").isNested(), is(false));
        assertThat(result.getTypeNamed("aNested"), is(Map.class));

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

        DataSchema partialSchema = SchemaBuilder.newInstance()
                .addRepeatingNamed("OrderLines", DataSchema.emptySchema())
                .build();

        JsonSchemaExtractor test = JsonSchemaExtractor.withPartialSchema(partialSchema);

        DataSchema schema = test.fromElement(rootObject);

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        assertThat(schema, is(expectedSchema));
    }
}