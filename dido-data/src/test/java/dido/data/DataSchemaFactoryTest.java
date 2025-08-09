package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DataSchemaFactoryTest {

    @Test
    void whenAddSchemaFieldThenOk() {

        DataSchemaFactory simpleSchemaFactory = DataSchemaFactory.newInstance();
        simpleSchemaFactory.addSchemaField(SchemaField.of(5, "Foo", Integer.class));
        DataSchema schemaSimple = simpleSchemaFactory.toSchema();

        assertThat(schemaSimple.getSchemaFieldAt(5), is(SchemaField.of(5, "Foo", Integer.class)));

        DataSchemaFactory nestedSchemaFactory = DataSchemaFactory.newInstance();
        nestedSchemaFactory.addSchemaField(SchemaField.ofNested(3, "Nested", schemaSimple));
        DataSchema schemaNested = nestedSchemaFactory.toSchema();

        assertThat(schemaNested.getSchemaFieldAt(3), is(SchemaField.ofNested(3, "Nested", schemaSimple)));

        DataSchemaFactory repeatingSchemaFactory = DataSchemaFactory.newInstance();
        repeatingSchemaFactory.addSchemaField(SchemaField.ofRepeating(3, "Nested", schemaSimple));
        DataSchema schemaRepeating = repeatingSchemaFactory.toSchema();

        assertThat(schemaRepeating.getSchemaFieldAt(3), is(SchemaField.ofRepeating(3, "Nested", schemaSimple)));
    }

    @Test
    void whenSameNameAndIndexAddedThenUpdated() {

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();
        schemaFactory.addSchemaField(SchemaField.of(5, "Foo", Integer.class));
        schemaFactory.addSchemaField(SchemaField.of(5, "Foo", String.class));
        DataSchema schema = schemaFactory.toSchema();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(5, "Foo", String.class)
                .build();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void whenSameNameAddedThenNameUpdated() {

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();
        schemaFactory.addSchemaField(SchemaField.of(5, "Foo", Integer.class));
        schemaFactory.addSchemaField(SchemaField.of(6, "Foo", String.class));
        schemaFactory.addSchemaField(SchemaField.of(0, "Foo", Double.class));
        DataSchema schema = schemaFactory.toSchema();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(5, "Foo", Integer.class)
                .addNamedAt(6, "Foo_", String.class)
                .addNamedAt(7, "Foo__", Double.class)
                .build();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void whenSameIndexAddedThenNameReplace() {

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();
        schemaFactory.addSchemaField(SchemaField.of(5, "Foo", Integer.class));
        schemaFactory.addSchemaField(SchemaField.of(5, "Bar", String.class));
        DataSchema schema = schemaFactory.toSchema();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(5, "Bar", String.class)
                .build();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void fieldRemoved() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstanceFrom(schema);

        assertThat(schemaFactory.removeAt(2), is(SchemaField.of(2, "qty", int.class)));
        assertThat(schemaFactory.removeNamed("price"), is(SchemaField.of(3, "price", double.class)));
        assertThat(schemaFactory.removeNamed("foo"), nullValue());
        assertThat(schemaFactory.removeAt(-27), nullValue());

        DataSchema newSchema = schemaFactory.toSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .build();

        assertThat(newSchema, is(expected));
    }

    @Test
    void testConcat() {

        DataSchema fruitSchema = DataSchema.builder()
                .addNamed("Id", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("GrocerId", String.class)
                .addNamed("Price", double.class)
                .build();

        DataSchema colourSchema = DataSchema.builder()
                .addNamed("Id", String.class)
                .addNamed("Colour", String.class)
                .build();

        DataSchema grocerSchema = DataSchema.builder()
                .addNamed("Id", String.class)
                .addNamed("Jones", String.class)
                .build();

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Id", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("GrocerId", String.class)
                .addNamed("Price", double.class)
                .addNamed("Id_", String.class)
                .addNamed("Colour", String.class)
                .addNamed("Id__", String.class)
                .addNamed("Jones", String.class)
                .build();

        SchemaFactory schemaFactory = SchemaFactory.newInstanceFrom(fruitSchema);
        schemaFactory.concat(colourSchema);
        schemaFactory.concat(grocerSchema);

        assertThat(schemaFactory.toSchema(), is(expectedSchema));
    }
}
