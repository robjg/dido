package dido.data.generic;

import dido.data.DataSchema;
import dido.data.schema.SchemaBuilder;
import dido.data.schema.SchemaDefs;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GenericSchemaBuilderTest {

    @Test
    void testAddSequentiallyNoFields() {

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addFieldAt(0, "a", String.class)
                .addFieldAt(0, "b", int.class)
                .addFieldAt(0, "c", double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(0));
        assertThat(schema.getFields(), contains("a", "b", "c"));
    }

    @Test
    void testAddSparseIndexes() {

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .addFieldAt(5, "a", String.class)
                .addField("b", int.class)
                .addFieldAt(20, "c", double.class)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(20));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(20));
        assertThat(schema.nextIndex(20), is(0));
        assertThat(schema.getFields(), contains("a", "b", "c"));

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
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFieldNames(), contains("fruit", "qty", "price"));
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
                .addFieldAt(7, "qty", int.class)
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

        GenericDataSchema<String> schema = GenericSchemaBuilder.forStringFields()
                .withSchemaDefs(SchemaDefs.newInstance())
                .withSchemaName("self")
                .addNestedRefField("node", "self")
                .build();

        assertThat(schema.getSchemaOf("node"), sameInstance(schema));
    }

    @Test
    void testAddRepeatingRefSchema() {

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

        GenericDataSchema<?> schema2 = GenericDataSchema.emptySchema(String.class);

        assertThat(schema1.hashCode(), is(schema2.hashCode()));
        assertThat(schema1, is(schema2));
    }

//    @Test
//    void whenAddSchemaFieldThenOk() {
//
//        DataSchema schemaSimple = SchemaBuilder.newInstance()
//                .addSchemaField(SchemaField.of(5, "Foo", Integer.class))
//                .build();
//
//        assertThat(schemaSimple.getSchemaFieldAt(5), is(SchemaField.of(5, "Foo", Integer.class)));
//
//        DataSchema schemaNested = SchemaBuilder.newInstance()
//                .addSchemaField(SchemaField.ofNested(3, "Nested", schemaSimple))
//                .build();
//
//        assertThat(schemaNested.getSchemaFieldAt(3), is(SchemaField.ofNested(3, "Nested", schemaSimple)));
//
//        DataSchema schemaRepeating = SchemaBuilder.newInstance()
//                .addSchemaField(SchemaField.ofRepeating(3, "Nested", schemaSimple))
//                .build();
//
//        assertThat(schemaRepeating.getSchemaFieldAt(3), is(SchemaField.ofRepeating(3, "Nested", schemaSimple)));
//    }

}