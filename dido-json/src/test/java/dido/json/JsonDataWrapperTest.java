package dido.json;

import com.google.gson.*;
import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.IndexedData;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class JsonDataWrapperTest {

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
    }

    public static class TestDeserializer implements JsonDeserializer<IndexedData<?>> {

        private final JsonDataWrapper test;

        public TestDeserializer(DataSchema<String> schema) {
            this.test = JsonDataWrapper.from(schema);
        }

        @Override
        public IndexedData<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) json;

            return test.wrap(jsonObject, context);
        }
    }

    @Test
    void testWithAllFields() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("aString", String.class)
                .addField("aBoolean", boolean.class)
                .addField("aByte", byte.class)
                .addField("aShort", short.class)
                .addField("anInt", int.class)
                .addField("aLong", long.class)
                .addField("aFloat", float.class)
                .addField("aDouble", double.class)
                .addField("aNumber", Number.class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IndexedData.class, new TestDeserializer(schema))
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData<?> result = gson.fromJson(json, IndexedData.class);

        assertThat(result.getStringAt(1), is("Apple"));
        assertThat(result.getBooleanAt(2), is(true));
        assertThat(result.getByteAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getShortAt(4), is(Short.MAX_VALUE));
        assertThat(result.getIntAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getLongAt(6), is(Long.MAX_VALUE));
        assertThat(result.getFloatAt(7), is(1.234F));
        assertThat(result.getDoubleAt(8), is(123456.78));
        assertThat(result.getObjectAt(9, Number.class).doubleValue(), is(67.2));
    }

    @Test
    void testWithAutoBoxedVersions() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addIndexedField(3, "aByte", Byte.class)
                .addField("aShort", Short.class)
                .addField("anInt", Integer.class)
                .addField("aLong", Long.class)
                .addField("aFloat", Float.class)
                .addField("aDouble", Double.class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IndexedData.class, new TestDeserializer(schema))
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData<?> result = gson.fromJson(json, IndexedData.class);

        assertThat(result.getByteAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getShortAt(4), is(Short.MAX_VALUE));
        assertThat(result.getIntAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getLongAt(6), is(Long.MAX_VALUE));
        assertThat(result.getFloatAt(7), is(1.234F));
        assertThat(result.getDoubleAt(8), is(123456.78));
    }

    @Test
    void testAllAsObjects() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("aString", String.class)
                .addField("aBoolean", boolean.class)
                .addField("aByte", byte.class)
                .addField("aShort", short.class)
                .addField("anInt", int.class)
                .addField("aLong", long.class)
                .addField("aFloat", float.class)
                .addField("aDouble", double.class)
                .addField("aNumber", Number.class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IndexedData.class, new TestDeserializer(schema))
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData<?> result = gson.fromJson(json, IndexedData.class);

        assertThat(result.getObjectAt(1), is("Apple"));
        assertThat(result.getObjectAt(2), is(true));
        assertThat(result.getObjectAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getObjectAt(4), is(Short.MAX_VALUE));
        assertThat(result.getObjectAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getObjectAt(6), is(Long.MAX_VALUE));
        assertThat(result.getObjectAt(7), is(1.234F));
        assertThat(result.getObjectAt(8), is(123456.78));
        assertThat(((Number) result.getObjectAt(9)).doubleValue(), is(67.2));
    }

    @Test
    void testWithAllFieldsAsNull() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("aString", String.class)
                .addField("aBoolean", boolean.class)
                .addField("aByte", byte.class)
                .addField("aShort", short.class)
                .addField("anInt", int.class)
                .addField("aLong", long.class)
                .addField("aFloat", float.class)
                .addField("aDouble", double.class)
                .addField("aNumber", Number.class)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IndexedData.class, new TestDeserializer(schema))
                .create();

        String json = "{}";

        IndexedData<?> result = gson.fromJson(json, IndexedData.class);


        assertThat(result.getStringAt(1), nullValue());
        assertThat(result.getBooleanAt(2), is(false));
        assertThat(result.getByteAt(3), is((byte) 0));
        assertThat(result.getShortAt(4), is((short) 0));
        assertThat(result.getIntAt(5), is(0));
        assertThat(result.getLongAt(6), is(0L));
        assertThat(result.getFloatAt(7), is(0.0F));
        assertThat(result.getDoubleAt(8), is(0.0));
        assertThat(result.getObjectAt(9, Number.class), nullValue());
    }
}