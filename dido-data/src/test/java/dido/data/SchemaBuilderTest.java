package dido.data;

import dido.data.generic.GenericDataSchema;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SchemaBuilderTest {

    @Test
    void testAddSequentiallyNoFields() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addAt(0, String.class)
                .addAt(0, int.class)
                .addAt(0, double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(0));
        assertThat(schema.getFieldNames().isEmpty(), is(true));
    }

    @Test
    void testAddSparseIndexes() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addAt(5, String.class)
                .add(int.class)
                .addAt(20, double.class)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(20));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(20));
        assertThat(schema.nextIndex(20), is(0));
        assertThat(schema.getFieldNames().isEmpty(), is(true));

        try {
            assertThat(schema.getFieldNameAt(2), nullValue());
            assertThat("Expected to fail", false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    void testAddFields() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFieldNames(), Matchers.contains("fruit", "qty", "price"));
    }

    @Test
    void testOverwriteWithSchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", String.class)
                .merge(correction)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteMiddleFieldSchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addField("qty", int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", double.class)
                .merge(correction)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteWithIndexOnlySchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addAt(2, int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", double.class)
                .merge(correction)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testMergeExtraFieldSchema() {

        DataSchema extra = SchemaBuilder.newInstance()
                .addField("colour", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .merge(extra)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .addField("colour", String.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testAddNestedSchema() {

        SchemaReference self = SchemaReference.blank();
        DataSchema schema = SchemaBuilder.newInstance()
                .addNestedField("node", self)
                .build();
        self.set(schema);

        assertThat(schema.getSchemaNamed("node"), sameInstance(schema));
    }

    @Test
    void testAddRepeatingSchema() {

        DataSchema nested = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingField("list", nested)
                .build();

        assertThat(schema.getSchemaNamed("list"), sameInstance(nested));
    }

    @Test
    void testEqualsAndHashCode() {

        DataSchema schema1 = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        DataSchema schema2 = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
    }

    @Test
    void testToString() {

        DataSchema schema1 = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1.toString(),
                is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double}"));
    }

    @Test
    void builtEmptySchemaEqualsEmptySchema() {

        DataSchema schema1 = SchemaBuilder.newInstance().build();

        DataSchema schema2 = GenericDataSchema.emptySchema();

        assertThat(schema1.hashCode(), is(schema2.hashCode()));
        assertThat(schema1, is(schema2));
    }

    @Test
    void whenAddSchemaFieldThenOk() {

        DataSchema schemaSimple = SchemaBuilder.newInstance()
                .addSchemaField(SchemaField.of(5, "Foo", Integer.class))
                .build();

        assertThat(schemaSimple.getSchemaFieldAt(5), is(SchemaField.of(5, "Foo", Integer.class)));

        DataSchema schemaNested = SchemaBuilder.newInstance()
                .addSchemaField(SchemaField.ofNested(3, "Nested", schemaSimple))
                .build();

        assertThat(schemaNested.getSchemaFieldAt(3), is(SchemaField.ofNested(3, "Nested", schemaSimple)));

        DataSchema schemaRepeating = SchemaBuilder.newInstance()
                .addSchemaField(SchemaField.ofRepeating(3, "Nested", schemaSimple))
                .build();

        assertThat(schemaRepeating.getSchemaFieldAt(3), is(SchemaField.ofRepeating(3, "Nested", schemaSimple)));
    }

}