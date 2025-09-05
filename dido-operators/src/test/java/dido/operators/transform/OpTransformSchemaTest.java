package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.MapData;
import dido.data.ReadSchema;
import dido.data.SchemaField;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests that {@link WriteTransformBuilder} creates the correct schema.
 */
class OpTransformSchemaTest {

    @Test
    void whenNewFieldsTheAdded() {

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder
                .forSchema(ReadSchema.emptySchema());

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "Two", long.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("One", int.class)
                .addNamed("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    /**
     * As Above but coping the empty schema.
     */
    @Test
    void mergeNewFieldsIntoEmptySchema() {

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .existingFields(true)
                .forSchema(ReadSchema.emptySchema());

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "Two", long.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("One", int.class)
                .addNamed("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void newFieldsReplaceToExistingByName() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "Here", int.class)
                .addNamedAt(20, "There", int.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "Here", String.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(10, "Here", String.class)
                .addNamedAt(20, "There", int.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void newFieldsReplaceToExistingByIndex() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "Here", int.class)
                .addNamedAt(20, "There", int.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(10, "Now", String.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(10, "Now", String.class)
                .addNamedAt(20, "There", int.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void newFieldsReplaceExistingByNameUsingAnotherExistingIndex() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "Here", int.class)
                .addNamedAt(20, "There", int.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(20, "Here", String.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(20, "Here", String.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void newFieldsAddedToExistingReindex() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "Here", int.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .reIndex(true)
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(0, "Two", long.class));
                    return df -> (d, o) -> {
                    };
                });

        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(1, "Here", int.class)
                .addNamedAt(2, "One", int.class)
                .addNamedAt(3, "Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void copyFieldsOutOfOrderExisting() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "One", int.class)
                .addNamedAt(20, "Two", long.class)
                .addNamedAt(30, "Three", double.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(30, "One", Integer.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(20, "Two", Long.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(10, "Three", Double.class));
                    return df -> (d, o) -> {
                    };
                });


        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(10, "Three", Double.class)
                .addNamedAt(20, "Two", Long.class)
                .addNamedAt(30, "One", Integer.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void copyFieldsOutOfOrderExistingReindex() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "One", int.class)
                .addNamedAt(20, "Two", long.class)
                .addNamedAt(30, "Three", double.class)
                .build();

        WriteTransformBuilder transformationBuilder = WriteTransformBuilder.with()
                .reIndex(true)
                .existingFields(true)
                .forSchema(existing);

        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(30, "One", Integer.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(20, "Two", Long.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addFieldWrite((s, sf) -> {
                    sf.addField(SchemaField.of(10, "Three", Double.class));
                    return df -> (d, o) -> {
                    };
                });


        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(1, "Three", Double.class)
                .addNamedAt(2, "Two", Long.class)
                .addNamedAt(3, "One", Integer.class)
                .build();

        assertThat(result, is(expected));
    }

}