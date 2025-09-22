package dido.data;

import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DidoDataTest {

    @Test
    void testEqualsSameIndices() {

        DidoData data1 = ArrayData.of("Apple", 5, 23.7);
        DidoData data2 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 23.7);

        assertThat(DidoData.equals(data1, data2), is(true));
    }

    @Test
    void testEqualsDifferentIndices() {

        DataSchema schema1 = DataSchema.builder()
                .addNamedAt(3, "Qty", int.class)
                .addNamedAt(15, "Customer", String.class)
                .build();

        DataSchema schema2 = DataSchema.builder()
                .addNamedAt(5, "Age", Integer.class)
                .addNamedAt(7, "Name", String.class)
                .build();

        DidoData data1 = DidoData.withSchema(schema1).of(42);
        DidoData data2 = DidoData.withSchema(schema2).of(42);

        MatcherAssert.assertThat(DidoData.equals(data1, data2), is(true));
        MatcherAssert.assertThat(data1.hashCode(), is(data2.hashCode()));

        MatcherAssert.assertThat(DidoData.strictlyEquals(data1, data2), is(false));

    }

}