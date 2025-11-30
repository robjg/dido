package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.*;
import dido.data.immutable.ArrayData;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

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

    @Test
    void testWithAllFields() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("aString", String.class)
                .addNamed("aBoolean", boolean.class)
                .addNamed("aByte", byte.class)
                .addNamed("aShort", short.class)
                .addNamed("anInt", int.class)
                .addNamed("aLong", long.class)
                .addNamed("aFloat", float.class)
                .addNamed("aDouble", double.class)
                .addNamed("aNumber", Number.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData result = gson.fromJson(json, DidoData.class);

        assertThat(result.getStringAt(1), is("Apple"));
        assertThat(result.getBooleanAt(2), is(true));
        assertThat(result.getByteAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getShortAt(4), is(Short.MAX_VALUE));
        assertThat(result.getIntAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getLongAt(6), is(Long.MAX_VALUE));
        assertThat(result.getFloatAt(7), is(1.234F));
        assertThat(result.getDoubleAt(8), is(123456.78));
        assertThat(((Number) result.getAt(9)).doubleValue(), is(67.2));
    }

    static class NumberNaNs {

        String aString = null;

        float aFloat = Float.NaN;

        double aDouble = Double.NaN;
    }

    @Test
    void testNaNs() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("aString", String.class)
                .addNamed("aFloat", float.class)
                .addNamed("aDouble", double.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .serializeSpecialFloatingPointValues()
                .create();

        String json = gson.toJson(new NumberNaNs());

        DidoData result = gson.fromJson(json, DidoData.class);

        assertThat(result.getStringAt(1), is("null"));
        assertThat(Float.isNaN(result.getFloatNamed("aFloat")), is(true));
        assertThat(Double.isNaN(result.getDoubleNamed("aDouble")), is(true));
    }

    @Test
    void testWithAutoBoxedVersions() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamedAt(3, "aByte", Byte.class)
                .addNamed("aShort", Short.class)
                .addNamed("anInt", Integer.class)
                .addNamed("aLong", Long.class)
                .addNamed("aFloat", Float.class)
                .addNamed("aDouble", Double.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData result = gson.fromJson(json, DidoData.class);

        assertThat(result.getByteAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getShortAt(4), is(Short.MAX_VALUE));
        assertThat(result.getIntAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getLongAt(6), is(Long.MAX_VALUE));
        assertThat(result.getFloatAt(7), is(1.234F));
        assertThat(result.getDoubleAt(8), is(123456.78));
    }

    @Test
    void testAllAsObjects() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("aString", String.class)
                .addNamed("aBoolean", boolean.class)
                .addNamed("aByte", byte.class)
                .addNamed("aShort", short.class)
                .addNamed("anInt", int.class)
                .addNamed("aLong", long.class)
                .addNamed("aFloat", float.class)
                .addNamed("aDouble", double.class)
                .addNamed("aNumber", Number.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        String json = gson.toJson(new AllFields());

        IndexedData result = gson.fromJson(json, DidoData.class);

        assertThat(result.getAt(1), is("Apple"));
        assertThat(result.getAt(2), is(true));
        assertThat(result.getAt(3), is(Byte.MAX_VALUE));
        assertThat(result.getAt(4), is(Short.MAX_VALUE));
        assertThat(result.getAt(5), is(Integer.MAX_VALUE));
        assertThat(result.getAt(6), is(Long.MAX_VALUE));
        assertThat(result.getAt(7), is(1.234F));
        assertThat(result.getAt(8), is(123456.78));
        assertThat(((Number) result.getAt(9)).doubleValue(), is(67.2));
    }

    @Test
    void testWithAllFieldsAsNull() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("aString", String.class)
                .addNamed("aBoolean", boolean.class)
                .addNamed("aByte", byte.class)
                .addNamed("aShort", short.class)
                .addNamed("anInt", int.class)
                .addNamed("aLong", long.class)
                .addNamed("aFloat", float.class)
                .addNamed("aDouble", double.class)
                .addNamed("aNumber", Number.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        String json = "{}";

        IndexedData result = gson.fromJson(json, DidoData.class);

        assertThat(result.getAt(1), nullValue());
        assertThat(result.getStringAt(1), is("null"));
        assertThat(result.hasAt(2), is(false));
        assertThat(result.hasAt(3), is(false));
        assertThat(result.hasAt(4), is(false));
        assertThat(result.hasAt(5), is(false));
        assertThat(result.hasAt(6), is(false));
        assertThat(result.hasAt(7), is(false));
        assertThat(result.hasAt(8), is(false));
        assertThat(((Number) result.getAt(9)), nullValue());
    }

    @Test
    void testNestedData() {

        String json = """
                { "OrderId": "A123",\s
                    "OrderLines": [\s
                      {"Fruit": "Apple", "Qty": 4},\s
                      {"Fruit": "Pear", "Qty": 5}
                    ]
                  }""";


        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        DidoData result = gson.fromJson(json, DidoData.class);

        IndexedData expectedData = ArrayData.withSchema(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.withSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.withSchema(nestedSchema)
                                        .of("Pear", 5)));

        assertThat(result, is(expectedData));

    }

    @Test
    void getters() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Colour", String.class)
                .build();

        Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create();

        String json = "{'Colour':'Red', 'Fruit':'Apple'}";

        DidoData data = gson.fromJson(json, DidoData.class);

        ReadSchema readSchema = (ReadSchema) data.getSchema();

        FieldGetter getter1 = readSchema.getFieldGetterAt(1);
        FieldGetter getter2 = readSchema.getFieldGetterAt(2);

        assertThat(getter1.get(data), is("Apple"));
        assertThat(getter2.get(data), is("Red"));
    }
}