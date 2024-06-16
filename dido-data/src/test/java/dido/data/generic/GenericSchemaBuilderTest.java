package dido.data.generic;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaField;
import dido.data.SchemaReference;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GenericSchemaBuilderTest {

    @Test
    void testAddSequentiallyNoFields() {

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
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
        assertThat(schema.getFields().isEmpty(), is(true));
    }

    @Test
    void testAddSparseIndexes() {

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
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
        assertThat(schema.getFields().isEmpty(), is(true));

        try {
            assertThat(schema.getFieldAt(2), nullValue());
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

        GenericDataSchema<String> correction = GenericSchemaBuilder.forStringFields()
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", String.class)
                .merge(correction)
                .build();

        GenericDataSchema<String> expected = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteMiddleFieldSchema() {

        GenericDataSchema<String> correction = GenericSchemaBuilder.forStringFields()
                .addField("qty", int.class)
                .build();

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", double.class)
                .merge(correction)
                .build();

        GenericDataSchema<String> expected = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteWithIndexOnlySchema() {

        GenericDataSchema<String> correction = GenericSchemaBuilder.forStringFields()
                .addAt(2, int.class)
                .build();

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", String.class)
                .addField("price", double.class)
                .merge(correction)
                .build();

        GenericDataSchema<String> expected = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testMergeExtraFieldSchema() {

        GenericDataSchema<String> extra = GenericSchemaBuilder.forStringFields()
                .addField("colour", String.class)
                .build();

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .merge(extra)
                .build();

        GenericDataSchema<String> expected = GenericSchemaBuilder.forStringFields()
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
        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addNestedField("node", self)
                .build();
        self.set(schema);

        assertThat(schema.getSchemaOf("node"), sameInstance(schema));
    }

    @Test
    void testAddRepeatingSchema() {

        GenericDataSchema<String> nested = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .build();

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addRepeatingField("list", nested)
                .build();

        assertThat(schema.getSchemaOf("list"), sameInstance(nested));
    }

    @Test
    void testEqualsAndHashCode() {

        GenericDataSchema<String> schema1 = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        GenericDataSchema<String> schema2 = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
    }

    @Test
    void testToString() {

        GenericDataSchema<String> schema1 = GenericSchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        assertThat(schema1.toString(),
                is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double}"));
    }

    @Test
    void builtEmptySchemaEqualsEmptySchema() {

        GenericDataSchema<?> schema1 = GenericSchemaBuilder.forStringFields().build();

        GenericDataSchema<?> schema2 = GenericDataSchema.emptySchema();

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