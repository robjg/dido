package dido.data;

import org.junit.jupiter.api.Test;

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
    }

    @Test
    void testConcatData() {


        DataBuilder<String> builder = MapRecord.newBuilderNoSchema();

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
        assertThat(result.getInt("qty"), is(2));
        assertThat(result.getDouble("price"), is(26.3));
        assertThat(result.getString("supplier"), is("Alice"));
        assertThat(result.getString("checked"), is("Bob"));
        assertThat(result.getBoolean("good"), is(true));
    }
}