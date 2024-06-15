package dido.operators;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class FlattenTest {

    @Test
    void testFlattenRepeatingField() {

        GenericDataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Qty", int.class)
                .build();

        GenericDataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", nestedSchema)
                .build();

        DidoData data = ArrayData.valuesFor(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 5)));

        List<DidoData> results = Flatten.fieldOfSchema("OrderLines", schema)
                .apply(data);

        GenericDataSchema<String> expectedSchema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addField("Fruit", String.class)
                .addField("Qty", int.class)
                .build();

        DidoData expected1 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Apple", 4);
        DidoData expected2 = ArrayData.valuesFor(expectedSchema)
                .of("A123", "Pear", 5);

        assertThat(results, contains(expected1, expected2));
    }

    @Test
    void testFlattenListField() {

        DidoData data = ArrayData.of("Foo", List.of(1, 2, 3), List.of("X", "Y"));

        List<DidoData> results = Flatten.indices(2, 3)
                .apply(data);

        DidoData expected1 = ArrayData.of("Foo", 1, "X");
        DidoData result1 = results.get(0);

        DataSchema expectedSchema1 = expected1.getSchema();
        DataSchema resultSchema1 = result1.getSchema();

        assertThat(resultSchema1, is(expectedSchema1));
        assertThat(result1, is(expected1));

        assertThat(results, contains(
                expected1,
                ArrayData.of("Foo", 2, "Y"),
                ArrayData.of("Foo", 3, null)));
    }
}
