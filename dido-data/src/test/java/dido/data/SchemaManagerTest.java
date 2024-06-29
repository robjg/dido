package dido.data;

import dido.data.generic.GenericDataSchema;
import dido.data.generic.GenericSchemaField;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SchemaManagerTest {

    static class PersonFields {
        static final String NAME = "Name";
        static final String AGE = "Age";
    }

    static class FamilyFields {
        static final String HUSBAND = "Husband";
        static final String WIFE = "Wife";
        static final String CHILDREN = "Children";
        static final String HOUSE = "House";
    }

    static class HouseFields {
        static final String BEDROOMS = "Bedrooms";
    }

    @Test
    void testCreateNestedSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("person")
                .addField(PersonFields.NAME, String.class)
                .addField(PersonFields.AGE, int.class)
                .addToManager()
                .newDefaultSchema()
                .addNestedField(FamilyFields.HUSBAND, "person")
                .addNestedField(FamilyFields.WIFE, "person")
                .addRepeatingField(FamilyFields.CHILDREN, "person")
                .addNestedField(FamilyFields.HOUSE)
                .addField(HouseFields.BEDROOMS, int.class)
                .addBack()
                .addToManager();

        DataSchema schema = schemaManager.getDefaultSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.getTypeAt(1), is(GenericSchemaField.NESTED_TYPE));
        assertThat(schema.getTypeNamed(FamilyFields.WIFE), is(GenericSchemaField.NESTED_TYPE));
        assertThat(schema.getTypeNamed(FamilyFields.CHILDREN), is(GenericSchemaField.NESTED_REPEATING_TYPE));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.getFieldNames(), contains(FamilyFields.HUSBAND, FamilyFields.WIFE,
                FamilyFields.CHILDREN, FamilyFields.HOUSE));

        DataSchema husband =
                schema.getSchemaNamed(FamilyFields.HUSBAND);
        assertThat(husband.getFieldNames(), contains(PersonFields.NAME, PersonFields.AGE));

        DataSchema children =
                schema.getSchemaNamed(FamilyFields.CHILDREN);
        assertThat(children.getFieldNames(), contains(PersonFields.NAME, PersonFields.AGE));

        DataSchema house =
                schema.getSchemaNamed(FamilyFields.HOUSE);
        assertThat(house.getFieldNames(), contains(HouseFields.BEDROOMS));

        assertThat(schemaManager.getSchema("person"), sameInstance(husband));
    }

    @Test
    void testRecurringSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("person")
                .addField("name", String.class)
                .addNestedField("father", "person")
                .addToManager();

        DataSchema schema = schemaManager.getSchema("person");

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(GenericSchemaField.NESTED_TYPE));
    }

    @Test
    void testRecurringRepeatingSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("node")
                .addRepeatingField("children", "node")
                .addToManager();

        DataSchema schema = schemaManager.getSchema("node");

        assertThat(schema.getTypeAt(1), is(GenericSchemaField.NESTED_REPEATING_TYPE));

    }

    @Test
    void testRepeatingNest() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addField("Name", String.class)
                .addRepeatingField("Hobbies")
                .addField("Title", String.class)
                .addField("Cost", double.class)
                .addBack()
                .addToManager();

        DataSchema schema = schemaManager.getDefaultSchema();

        DataSchema expectedNested = SchemaBuilder.newInstance()
                .addNamed("Title", String.class)
                .addNamed("Cost", double.class)
                .build();

        assertThat(schema.getTypeAt(2), is(GenericSchemaField.NESTED_REPEATING_TYPE));

        DataSchema nested = schema.getSchemaNamed("Hobbies");

        assertThat(nested, is(expectedNested));
    }

    @Test
    void testEmptySchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addToManager();

        DataSchema dataSchema = schemaManager.getDefaultSchema();

        assertThat(dataSchema, is(GenericDataSchema.emptySchema()));
    }

    @Test
    void testEmptyNestedSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addNestedField("Foo")
                .addBack()
                .addToManager();

        DataSchema dataSchema = schemaManager.getDefaultSchema();

        SchemaField nestedField = dataSchema.getSchemaFieldNamed("Foo");

        assertThat(nestedField.getNestedSchema(), is(GenericDataSchema.emptySchema()));
        assertThat(nestedField.isRepeating(), is(false));
    }

    @Test
    void testEmptyRepeatingSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newDefaultSchema()
                .addRepeatingField("Foo")
                .addBack()
                .addToManager();

        DataSchema dataSchema = schemaManager.getDefaultSchema();

        SchemaField nestedField = dataSchema.getSchemaFieldNamed("Foo");

        assertThat(nestedField.getNestedSchema(), is(GenericDataSchema.emptySchema()));
        assertThat(nestedField.isRepeating(), is(true));
    }
}