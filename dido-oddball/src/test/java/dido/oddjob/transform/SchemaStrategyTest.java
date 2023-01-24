package dido.oddjob.transform;

import dido.data.DataSchema;
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

        DataSchema<String> result =
                SchemaStrategy.NEW.newSchemaFrom(DataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema<String> expected = SchemaBuilder.forStringFields()
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

        DataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(DataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema<String> expected = SchemaBuilder.forStringFields()
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

        DataSchema<String> existing = SchemaBuilder.forStringFields()
                .addFieldAt(10, "Here", int.class)
                .build();

        List<Integer> existingIndices = new ArrayList<>();
        DataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        existingIndices::add);

        DataSchema<String> expected = SchemaBuilder.forStringFields()
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

        DataSchema<String> existing = SchemaBuilder.forStringFields()
                .addFieldAt(10, "One", int.class)
                .addFieldAt(20, "Two", long.class)
                .addFieldAt(30, "Three", double.class)
                .build();

        DataSchema<String> result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema<String> expected = SchemaBuilder.forStringFields()
                .addFieldAt(10, "Three", Double.class)
                .addFieldAt(20, "Two", Long.class)
                .addFieldAt(30, "One", Integer.class)
                .build();

        assertThat(result, is(expected));

    }

}