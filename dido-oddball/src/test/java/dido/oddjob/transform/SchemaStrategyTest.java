package dido.oddjob.transform;

import dido.data.GenericDataSchema;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class SchemaStrategyTest {

    @Test
    void testNewFields() {

        List<SchemaFieldOptions<String>> fields =
                List.of(SchemaFieldOptions.of(0, "One", int.class),
                        SchemaFieldOptions.of(0, "Two", long.class));

        GenericDataSchema<String> result =
                SchemaStrategy.NEW.newSchemaFrom(GenericDataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        GenericDataSchema<String> expected = SchemaBuilder.forStringFields()
                .addField("One", int.class)
                .addField("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void testMergeNewFieldsIntoEmptySchema() {

        List<SchemaFieldOptions<String>> fields =
                List.of(SchemaFieldOptions.of(0, "One", int.class),
                        SchemaFieldOptions.of(0, "Two", long.class));

        GenericDataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(GenericDataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        GenericDataSchema<String> expected = SchemaBuilder.forStringFields()
                .addField("One", int.class)
                .addField("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void testNewFieldsAddedToExisting() {

        List<SchemaFieldOptions<String>> fields =
                List.of(SchemaFieldOptions.of(0, "One", int.class),
                        SchemaFieldOptions.of(0, "Two", long.class));

        GenericDataSchema<String> existing = SchemaBuilder.forStringFields()
                .addFieldAt(10, "Here", int.class)
                .build();

        List<Integer> existingIndices = new ArrayList<>();
        GenericDataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        existingIndices::add);

        GenericDataSchema<String> expected = SchemaBuilder.forStringFields()
                .addFieldAt(10, "Here", int.class)
                .addFieldAt(11, "One", int.class)
                .addFieldAt(12, "Two", long.class)
                .build();

        assertThat(result, is(expected));
        assertThat(existingIndices, contains(10));
    }

    @Test
    void testCopyFieldsOutOfOrderExisting() {

        List<SchemaFieldOptions<String>> fields =
                List.of(SchemaFieldOptions.of(30, "One", Integer.class),
                        SchemaFieldOptions.of(20, "Two", Long.class),
                        SchemaFieldOptions.of(10, "Three", Double.class));

        GenericDataSchema<String> existing = SchemaBuilder.forStringFields()
                .addFieldAt(10, "One", int.class)
                .addFieldAt(20, "Two", long.class)
                .addFieldAt(30, "Three", double.class)
                .build();

        GenericDataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        GenericDataSchema<String> expected = SchemaBuilder.forStringFields()
                .addFieldAt(10, "Three", Double.class)
                .addFieldAt(20, "Two", Long.class)
                .addFieldAt(30, "One", Integer.class)
                .build();

        assertThat(result, is(expected));

    }

}