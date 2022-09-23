package dido.operators;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class FlattenTest {

    @Test
    void testFlattenRepeatingField() {

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Qty", int.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", nestedSchema)
                .build();

        IndexedData<String> data = ArrayData.valuesFor(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 5)));

        List<GenericData<String>> results = Flatten.fieldOfSchema("OrderLines", schema)
                .apply(data);

        DataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addField("Fruit", String.class)
                .addField("Qty", int.class)
                .build();

        IndexedData<String> expected1 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Apple", 4);
        IndexedData<String> expected2 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Pear", 5);

        assertThat(results, contains(expected1, expected2));
    }

    @Test
    void testFlattenListField() {

        GenericData<Object> data = ArrayData.of("Foo", List.of(1, 2, 3), List.of("X", "Y"));

        List<GenericData<Object>> results = Flatten.indices(2, 3)
                .apply(data);

        assertThat(results, contains(
                ArrayData.of("Foo", 1, "X"),
                ArrayData.of("Foo", 2, "Y"),
                ArrayData.of("Foo", 3, null)));
    }
}
