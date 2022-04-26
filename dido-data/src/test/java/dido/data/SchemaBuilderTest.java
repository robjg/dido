package dido.data;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SchemaBuilderTest {

    @Test
    void testAddSequentiallyNoFields() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addIndex(0, String.class)
                .addIndex(0, int.class)
                .addIndex(0, double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(0));
        assertThat(schema.getFields().isEmpty(), is(true));
    }

    @Test
    void testAddSparseIndexes() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addIndex(5, String.class)
                .addNextIndex(int.class)
                .addIndex(20, double.class)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(20));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(20));
        assertThat(schema.nextIndex(20), is(0));
        assertThat(schema.getFields().isEmpty(), is(true));
        assertThat(schema.getFieldAt(2), nullValue());
    }

    @Test
    void testAddFields() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getType("fruit"), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFields(), Matchers.contains("fruit", "qty", "price"));
    }

    @Test
    void testOverwriteWithSchema() {

        DataSchema<String> correction = SchemaBuilder.forStringFields()
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", String.class)
                .merge(correction)
                .build();

        DataSchema<String> expected = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteMiddleFieldSchema() {

        DataSchema<String> correction = SchemaBuilder.forStringFields()
                .addField("qty", int.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", double.class)
                .merge(correction)
                .build();

        DataSchema<String> expected = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testAddNestedSchema() {

        AtomicReference<DataSchema<String>> self = new AtomicReference<>();
        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addNestedField("node", self::get)
                .build();
        self.set(schema);

        assertThat(schema.getSchema("node"), Matchers.sameInstance(schema));
    }

    @Test
    void testEqualsAndHashCode() {

        DataSchema<String> schema1 = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        DataSchema<String> schema2 = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
    }

    @Test
    void testToString() {

        DataSchema<String> schema1 = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1.toString(),
                is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double}"));
    }

}