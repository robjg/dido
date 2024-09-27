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

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        DidoData data = ArrayData.valuesForSchema(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesForSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesForSchema(nestedSchema)
                                        .of("Pear", 5)));

        List<DidoData> results = Flatten.fieldOfSchema("OrderLines", schema)
                .apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .build();

        DidoData expected1 = ArrayData.valuesForSchema(expectedSchema)
                .of("A123", "Apple", 4);
        DidoData expected2 = ArrayData.valuesForSchema(expectedSchema)
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
