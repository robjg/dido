package dido.data;

import dido.data.generic.GenericDataSchema;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SchemaBuilderTest {

    @Test
    void addSequentiallyNoFieldNames() {

        DataSchema schema = SchemaBuilder.newInstance()
                .add(String.class)
                .add(int.class)
                .add(double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getTypeAt(3), is(double.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(0));

        assertThat(schema.getFieldNames(), contains("[1]", "[2]", "[3]"));
    }

    @Test
    void addSparseIndexes() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addAt(5, String.class)
                .add(String.class)
                .addAt(20, double.class)
                .addAt(6, int.class)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(20));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.nextIndex(5), is(6));
        assertThat(schema.nextIndex(6), is(20));
        assertThat(schema.nextIndex(20), is(0));
        assertThat(schema.getFieldNames(), contains("[5]", "[6]", "[20]"));

        try {
            assertThat(schema.getFieldNameAt(2), nullValue());
            assertThat("Expected to fail", false);
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    void addFieldsSequentiallyByName() {

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
        assertThat(schema.getFieldNames(), Matchers.contains("fruit", "qty", "price"));
    }

    @Test
    void addFieldsOfSameName() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("a", String.class)
                .addNamed("a", int.class)
                .addNamed("a", double.class)
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("a"), is(String.class));
        assertThat(schema.getTypeNamed("a_"), is(int.class));
        assertThat(schema.getTypeNamed("a__"), is(double.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFieldNames(), Matchers.contains("a", "a_", "a__"));
    }

    @Test
    void addFieldsSameNameSameIndex() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamedAt(5, "a", String.class)
                .addNamedAt(5, "a", int.class)
                .addNamedAt(5, "a", double.class)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(5));
        assertThat(schema.getTypeAt(5), is(double.class));
        assertThat(schema.getTypeNamed("a"), is(double.class));
        assertThat(schema.nextIndex(5), is(0));
        assertThat(schema.getFieldNames(), Matchers.contains("a"));
    }

    @Test
    void removeByIndex() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addAt(5, String.class)
                .add(String.class)
                .addAt(20, double.class)
                .removeAt(6)
                .build();

        assertThat(schema.firstIndex(), is(5));
        assertThat(schema.lastIndex(), is(20));
        assertThat(schema.getTypeAt(5), is(String.class));
        assertThat(schema.nextIndex(5), is(20));
        assertThat(schema.nextIndex(20), is(0));
        assertThat(schema.getFieldNames(), contains("[5]", "[20]"));

    }

    @Test
    void removeFieldsByName() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .removeNamed("qty")
                .build();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.nextIndex(1), is(3));
        assertThat(schema.nextIndex(3), is(0));
        assertThat(schema.getFieldNames(), Matchers.contains("fruit", "price"));
    }

    @Test
    void testOverwriteWithSchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", String.class)
                .addNamed("price", String.class)
                .merge(correction)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteMiddleFieldSchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addNamed("qty", int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", String.class)
                .addNamed("price", double.class)
                .merge(correction)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testOverwriteWithIndexOnlySchema() {

        DataSchema correction = SchemaBuilder.newInstance()
                .addAt(2, int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", String.class)
                .addNamed("price", double.class)
                .merge(correction)
                .build();

        // The index is ignored now.
        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(4,"[2]", int.class)
                .addNamedAt(1, "fruit", String.class)
                .addNamedAt(2, "qty", String.class)
                .addNamedAt(3, "price", double.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testMergeExtraFieldSchema() {

        DataSchema extra = SchemaBuilder.newInstance()
                .addNamed("colour", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .merge(extra)
                .build();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .addNamed("colour", String.class)
                .build();

        assertThat(schema, is(expected));
    }

    @Test
    void testAddNestedSchema() {

        DataSchema item = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNestedNamed("item", item)
                .build();

        assertThat(schema.getTypeNamed("item"), is(SchemaField.NESTED_TYPE));
        assertThat(schema.getSchemaFieldNamed("item").isNested(), is(true));
        assertThat(schema.getSchemaFieldNamed("item").isRepeating(), is(false));
        assertThat(schema.getSchemaNamed("item"), sameInstance(item));
    }

    @Test
    void testAddNestedSchemaRef() {

        SchemaReference self = SchemaReference.blank();
        DataSchema schema = SchemaBuilder.newInstance()
                .addNestedNamed("node", self)
                .build();
        self.set(schema);

        assertThat(schema.getSchemaNamed("node"), sameInstance(schema));
    }

    @Test
    void testAddRepeatingSchema() {

        DataSchema nested = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingNamed("list", nested)
                .build();

        assertThat(schema.getTypeNamed("list"), is(SchemaField.NESTED_REPEATING_TYPE));
        assertThat(schema.getSchemaFieldNamed("list").isNested(), is(true));
        assertThat(schema.getSchemaFieldNamed("list").isRepeating(), is(true));
        assertThat(schema.getSchemaNamed("list"), sameInstance(nested));
    }

    @Test
    void testAddRepeatingSchemaRef() {

        SchemaReference ref = SchemaReference.blank();

        DataSchema nested = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingNamed("list", ref)
                .build();
        ref.set(nested);

        assertThat(schema.getTypeNamed("list"), is(SchemaField.NESTED_REPEATING_TYPE));
        assertThat(schema.getSchemaFieldNamed("list").isNested(), is(true));
        assertThat(schema.getSchemaFieldNamed("list").isRepeating(), is(true));
        assertThat(schema.getSchemaNamed("list"), sameInstance(nested));
    }

    @Test
    void testEqualsAndHashCode() {

        DataSchema schema1 = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchema schema2 = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        assertThat(schema1, is(schema2));
        assertThat(schema1.hashCode(), is(schema2.hashCode()));
    }

    @Test
    void testToString() {

        DataSchema schema1 = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
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