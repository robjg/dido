package dido.operators.transform;

import dido.data.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests that {@link OpTransformBuilder} creates the correct schema.
 */
class OpTransformSchemaTest {

    @Test
    void whenNewFieldsTheAdded() {

        OpTransformBuilder transformationBuilder = OpTransformBuilder
                .forSchema(ReadSchema.emptySchema());

        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addOp((s, sf) -> {
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

        OpTransformBuilder transformationBuilder = OpTransformBuilder.with()
                .copy(true)
                .forSchema(ReadSchema.emptySchema());

        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addOp((s, sf) -> {
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
    void newFieldsAddedToExisting() {

        DataSchema existing = MapData.schemaBuilder()
                .addNamedAt(10, "Here", int.class)
                .build();

        OpTransformBuilder transformationBuilder = OpTransformBuilder.with()
                .copy(true)
                .forSchema(existing);

        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(0, "One", int.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addOp((s, sf) -> {
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

        OpTransformBuilder transformationBuilder = OpTransformBuilder.with()
                .copy(true)
                .forSchema(existing);

        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(30, "One", Integer.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(20, "Two", Long.class));
                    return df -> (d, o) -> {
                    };
                });
        transformationBuilder
                .addOp((s, sf) -> {
                    sf.addField(SchemaField.of(10, "Three", Double.class));
                    return df -> (d, o) -> {
                    };
                });


        DidoTransform didoTransform = transformationBuilder.build();

        DataSchema result = didoTransform.getResultantSchema();

        // Note this doesn't quite work because when adding three it
        // removes the operation at the old index of 3, which is now "One".
        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(1, "Three", Double.class)
                .addNamedAt(2, "Two", Long.class)
                .build();

        assertThat(result, is(expected));
    }

}