package dido.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
                .add()
                .newDefaultSchema(FamilyFields.class)
                .addNestedField(FamilyFields.HUSBAND, "person")
                .addNestedField(FamilyFields.WIFE, "person")
                .addNestedRepeatingField(FamilyFields.CHILDREN, "person")
                .addNestedField(FamilyFields.HOUSE, SchemaBuilder.forFieldType(HouseFields.class)
                        .addField(HouseFields.BEDROOMS, int.class)
                        .build())
                .add();



    }
}