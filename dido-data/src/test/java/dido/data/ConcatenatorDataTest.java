package dido.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class ConcatenatorDataTest {

    @Test
    void testConcatSchema() {

        DataSchema<String> schema1 = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        DataSchema<String> schema2 = SchemaBuilder.forStringFields()
                .addField("supplier", String.class)
                .build();

        DataSchema<String> schema3 = SchemaBuilder.forStringFields()
                .addField("checked", String.class)
                .addField("good", boolean.class)
                .build();

        DataSchema<String> schema = Concatenator.fromSchemas(schema1, schema2, schema3)
                .getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(6));

        assertThat(schema.getFieldAt(1), is("fruit"));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getType("fruit"), is(String.class));
        assertThat(schema.getIndex("fruit"), is(1));

        assertThat(schema.getFieldAt(2), is("qty"));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getType("qty"), is(int.class));
        assertThat(schema.getIndex("qty"), is(2));

        assertThat(schema.getFieldAt(4), is("supplier"));
        assertThat(schema.getTypeAt(4), is(String.class));
        assertThat(schema.getType("supplier"), is(String.class));
        assertThat(schema.getIndex("supplier"), is(4));

        assertThat(schema.getFieldAt(5), is("checked"));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.getType("checked"), is(String.class));
        assertThat(schema.getIndex("checked"), is(5));

        assertThat(schema.getFieldAt(6), is("good"));
        assertThat(schema.getTypeAt(6), is(boolean.class));
        assertThat(schema.getType("good"), is(boolean.class));
        assertThat(schema.getIndex("good"), is(6));

        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(5));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(0));

        assertThat(schema.getFields(), contains("fruit", "qty", "price", "supplier", "checked", "good"));

        assertThat(schema.toString(), is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double, [4:supplier]=java.lang.String, [5:checked]=java.lang.String, [6:good]=boolean}"));
    }

    @Test
    void testConcatData() {


        DataBuilder<String> builder = MapData.newBuilderNoSchema();

        GenericData<String> data1 = builder
                .setString("type", "apple")
                .setInt("qty", 2)
                .setDouble("price", 26.3)
                .build();

        GenericData<String> data2 = builder
                .setString("supplier", "Alice")
                .build();

        GenericData<String> data3 = builder
                .setString("checked", "Bob")
                .setBoolean("good", true)
                .build();

        GenericData<String> result = Concatenator.of(data1, data2, data3);

        assertThat(result.getString("type"), is("apple"));
        assertThat(result.getStringAt(1), is("apple"));
        assertThat(result.getInt("qty"), is(2));
        assertThat(result.getIntAt(2), is(2));
        assertThat(result.getDouble("price"), is(26.3));
        assertThat(result.getDoubleAt(3), is(26.3));
        assertThat(result.getString("supplier"), is("Alice"));
        assertThat(result.getStringAt(4), is("Alice"));
        assertThat(result.getString("checked"), is("Bob"));
        assertThat(result.getStringAt(5), is("Bob"));
        assertThat(result.getBoolean("good"), is(true));
        assertThat(result.getBooleanAt(6), is(true));

        assertThat(result.toString(), is("{[1:type]=apple, [2:qty]=2, [3:price]=26.3, [4:supplier]=Alice, [5:checked]=Bob, [6:good]=true}"));
    }

    @Test
    void testOtherTypes() {

        DataBuilder<String> builder = MapData.newBuilderNoSchema();

        builder.setString("first", "Ignored" );
        GenericData<String> data1 = builder.build();

        builder.setObject("object", Arrays.asList("Foo") );
        GenericData<String> data2 = builder.build();

        builder.setBoolean("boolean", true );
        GenericData<String> data3 = builder.build();

        builder.setByte("byte", (byte) 32 );
        GenericData<String> data4 = builder.build();

        builder.setChar("char", 'A' );
        GenericData<String> data5 = builder.build();

        builder.setShort("short", (short) 42 );
        GenericData<String> data6 = builder.build();

        builder.setLong("long", 42L );
        GenericData<String> data7 = builder.build();

        builder.setFloat("float", 42.42F );
        GenericData<String> data8 = builder.build();

        GenericData<String> result1 = Concatenator.of(data1, data2, data3, data4, data5, data6, data7, data8);

        assertThat(result1.get("object"), is(Arrays.asList("Foo")));
        assertThat(result1.getAt(2), is(Arrays.asList("Foo")));
        assertThat(result1.getAs("object", List.class), is(Arrays.asList("Foo")));
        assertThat(result1.getAtAs(2, List.class), is(Arrays.asList("Foo")));
        assertThat(result1.getBoolean("boolean"), is(true));
        assertThat(result1.getBooleanAt(3), is(true));
        assertThat(result1.getByte("byte"), is((byte) 32));
        assertThat(result1.getByteAt(4), is((byte) 32));
        assertThat(result1.getChar("char"), is('A'));
        assertThat(result1.getCharAt(5), is('A'));
        assertThat(result1.getChar("char"), is('A'));
        assertThat(result1.getCharAt(5), is('A'));
        assertThat(result1.getShort("short"), is((short) 42));
        assertThat(result1.getShortAt(6), is((short) 42));
        assertThat(result1.getLong("long"), is(42L));
        assertThat(result1.getLongAt(7), is(42L));
        assertThat(result1.getFloat("float"), is(42.42F));
        assertThat(result1.getFloatAt(8), is(42.42F));

        GenericData<String> result2 = Concatenator.of(data1, data2, data3, data4, data5, data6, data7, data8);

        assertThat(result1, is(result2));
        assertThat(result1.hashCode(), is(result2.hashCode()));
    }
}