package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SchemaManagerTest {

    enum PersonFields {
        NAME,
        AGE,
    }

    enum FamilyFields {
        HUSBAND,
        WIFE,
        CHILDREN,
        HOUSE
    }

    enum HouseFields {
        BEDROOMS
    }

    @Test
    void testCreateNestedSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("person", PersonFields.class)
                .addField(PersonFields.NAME, String.class)
                .addField(PersonFields.AGE, int.class)
                .addToManager()
                .newDefaultSchema(FamilyFields.class)
                .addNestedField(FamilyFields.HUSBAND, "person")
                .addNestedField(FamilyFields.WIFE, "person")
                .addNestedRepeatingField(FamilyFields.CHILDREN, "person")
                .addNestedField(FamilyFields.HOUSE, HouseFields.class)
                        .addField(HouseFields.BEDROOMS, int.class)
                        .addBack()
                .addToManager();

        DataSchema<FamilyFields> schema = schemaManager.getDefaultSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.getTypeAt(1), is(SchemaBuilder.NESTED_TYPE));
        assertThat(schema.getType(FamilyFields.WIFE), is(SchemaBuilder.NESTED_TYPE));
        assertThat(schema.getType(FamilyFields.CHILDREN), is(SchemaBuilder.NESTED_REPEATING_TYPE));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.getFields(), contains(FamilyFields.HUSBAND, FamilyFields.WIFE,
                FamilyFields.CHILDREN, FamilyFields.HOUSE));

        DataSchema<PersonFields> husband = schema.getSchema(FamilyFields.HUSBAND);
        assertThat(husband.getFields(), contains(PersonFields.NAME, PersonFields.AGE));

        DataSchema<PersonFields> children = schema.getSchema(FamilyFields.CHILDREN);
        assertThat(children.getFields(), contains(PersonFields.NAME, PersonFields.AGE));

        DataSchema<HouseFields> house = schema.getSchema(FamilyFields.HOUSE);
        assertThat(house.getFields(), contains(HouseFields.BEDROOMS));

        assertThat(schemaManager.getSchema("person"), sameInstance(husband));
    }

    @Test
    void testRecurringSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("person", String.class)
                .addField("name", String.class)
                .addNestedField("father", "person")
                .addToManager();

        DataSchema<String> schema = schemaManager.getSchema("person");

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(SchemaBuilder.NESTED_TYPE));
    }

    @Test
    void testRecurringRepeatingSchema() {

        SchemaManager schemaManager = SchemaManager.newInstance()
                .newSchema("node", String.class)
                .addNestedRepeatingField("children", "node")
                .addToManager();

        DataSchema<String> schema = schemaManager.getSchema("node");

        assertThat(schema.getTypeAt(1), is(SchemaBuilder.NESTED_REPEATING_TYPE));

    }
}