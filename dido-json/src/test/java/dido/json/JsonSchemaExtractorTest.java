package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Date;

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
            this.test = JsonSchemaExtractor.from(schema);
        }

        @Override
        public DataSchema<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) json;

            return test.fromElement(jsonObject);
        }
    }

    @Test
    void testWithAllFields() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DataSchema.class, new TestDeserializer(schema))
                .create();

        String json = gson.toJson(new AllFields());

        DataSchema<String> result = gson.fromJson(json, DataSchema.class);

        assertThat(result.getType("aString"), is(String.class));
        assertThat(result.getType("aBoolean"), is(boolean.class));
        assertThat(result.getType("aByte"), is(double.class));
        assertThat(result.getType("aShort"), is(double.class));
        assertThat(result.getType("anInt"), is(double.class));
        assertThat(result.getType("aLong"), is(double.class));
        assertThat(result.getType("aFloat"), is(double.class));
        assertThat(result.getType("aDouble"), is(double.class));
        assertThat(result.getType("aNumber"), is(double.class));
        assertThat(result.getType("anArray"), is(Object[].class));
        assertThat(result.getType("aDate"), is(String.class));

        assertThat(result.getSchemaField("aNested").isNested(), is(true));

        DataSchema<String> nestedSchema = result.getSchema("aNested");
        assertThat(nestedSchema.getType("nestedString"), is(String.class));

        assertThat(result.firstIndex(), is(1));
        assertThat(result.lastIndex(), is(12));
    }
}