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

        DidoData data = ArrayData.withSchema(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.withSchema(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.withSchema(nestedSchema)
                                        .of("Pear", 5)));

        List<DidoData> results = Flatten.fieldOfSchema("OrderLines", schema)
                .apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .build();

        List<DidoData> expected = ArrayData.withSchema(expectedSchema)
                .many()
                .of("A123", "Apple", 4)
                .of("A123", "Pear", 5)
                .toList();

        assertThat(results, is(expected));
    }

    @Test
    void testFlattenListField() {

        DidoData data = ArrayData.of("Foo", List.of(1, 2, 3), List.of("X", "Y"));

        List<DidoData> results = Flatten.indices(2, 3)
                .apply(data);

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .add(String.class)
                .add(Object.class)
                .add(Object.class)
                .build();

        DidoData result1 = results.get(0);

        DataSchema resultSchema1 = result1.getSchema();

        assertThat(resultSchema1, is(expectedSchema));

        FromValues values = ArrayData.withSchema(expectedSchema);

        assertThat(results, contains(
                values.of("Foo", 1, "X"),
                values.of("Foo", 2, "Y"),
                values.of("Foo", 3, null)));
    }
}
