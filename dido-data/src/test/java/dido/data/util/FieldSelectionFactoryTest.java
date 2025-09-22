package dido.data.util;

import dido.data.DataSchema;
import dido.data.immutable.ArrayData;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldSelectionFactoryTest {

    DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Id", String.class)
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .addNamed("Colour", String.class)
            .build();

    @Test
    void allMethods() {

        FieldSelectionFactory<int[]> test = new FieldSelectionFactory<>(schema, Function.identity());

        assertThat(test.withIndices(2, 4), is(new int[] {2, 4}));
        assertThat(test.withNames("Fruit", "Price"), is(new int[] {2, 4}));
        assertThat(test.withNames(Set.of("Fruit", "Price")), is(new int[] {2, 4}));
        assertThat(test.excludingIndices(1, 3, 5), is(new int[] {2, 4}));
        assertThat(test.excludingNames("Id", "Qty", "Colour"), is(new int[] {2, 4}));
        assertThat(test.excludingNames(Set.of("Id", "Qty", "Colour")), is(new int[] {2, 4}));
    }

    @Test
    void empty() {

        FieldSelectionFactory<int[]> test = new FieldSelectionFactory<>(schema, Function.identity());
        assertThat(test.withNames(), is(new int[0]));
        assertThat(test.excludingNames(), is(new int[] {1, 2, 3, 4, 5}));

    }
}