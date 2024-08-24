package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class SchemaStrategyTest {

    @Test
    void testNewFields() {

        List<SchemaField> fields =
                List.of(SchemaField.of(0, "One", int.class),
                        SchemaField.of(0, "Two", long.class));

        DataSchema result =
                SchemaStrategy.NEW.newSchemaFrom(DataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("One", int.class)
                .addNamed("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void testMergeNewFieldsIntoEmptySchema() {

        List<SchemaField> fields =
                List.of(SchemaField.of(0, "One", int.class),
                        SchemaField.of(0, "Two", long.class));

        DataSchema result =
                SchemaStrategy.MERGE.newSchemaFrom(DataSchema.emptySchema(), fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamed("One", int.class)
                .addNamed("Two", long.class)
                .build();

        assertThat(result, is(expected));
    }

    @Test
    void testNewFieldsAddedToExisting() {

        List<SchemaField> fields =
                List.of(SchemaField.of(0, "One", int.class),
                        SchemaField.of(0, "Two", long.class));

        DataSchema existing = SchemaBuilder.newInstance()
                .addNamedAt(10, "Here", int.class)
                .build();

        List<Integer> existingIndices = new ArrayList<>();
        DataSchema result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        existingIndices::add);

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(10, "Here", int.class)
                .addNamedAt(11, "One", int.class)
                .addNamedAt(12, "Two", long.class)
                .build();

        assertThat(result, is(expected));
        assertThat(existingIndices, contains(10));
    }

    @Test
    void testCopyFieldsOutOfOrderExisting() {

        List<SchemaField> fields =
                List.of(SchemaField.of(30, "One", Integer.class),
                        SchemaField.of(20, "Two", Long.class),
                        SchemaField.of(10, "Three", Double.class));

        DataSchema existing = SchemaBuilder.newInstance()
                .addNamedAt(10, "One", int.class)
                .addNamedAt(20, "Two", long.class)
                .addNamedAt(30, "Three", double.class)
                .build();

        DataSchema result =
                SchemaStrategy.MERGE.newSchemaFrom(existing, fields,
                        i -> { throw new RuntimeException("Unexpected"); });

        DataSchema expected = SchemaBuilder.newInstance()
                .addNamedAt(10, "Three", Double.class)
                .addNamedAt(20, "Two", Long.class)
                .addNamedAt(30, "One", Integer.class)
                .build();

        assertThat(result, is(expected));
    }

}