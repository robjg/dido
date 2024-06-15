package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class ArrayDataTest {

    @Test
    void testEmptyData() {

        DidoData data = ArrayData.of();

        DataSchema schema = data.getSchema();

        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
    }

    @Test
    void testSimpleData() {

        DidoData data = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data.getStringAt(1), is("Apple"));
        assertThat(data.hasIndex(2), is(false));
        assertThat(data.getIntAt(3), is(15));
        assertThat(data.getDoubleAt(4), is(26.5));

        DataSchema schema = data.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(0));
    }

    @Test
    void testToString() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1.toString(), is("[Apple, null, 15, 26.5]"));
    }

    @Test
    void testEqualsAndHashCode() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        DidoData data2 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }

    @Test
    void testBuilderOf() {

        DidoData data1 = ArrayData.of("Apple", null, 15, 26.5);

        DidoData data2 = ArrayData.valuesFor(data1.getSchema())
                .of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
    }

    @Test
    void whenUsingBuilderThenSchemaDerived() {

        DidoData data1 = ArrayData.<String>newBuilder()
                .setString("Fruit", "Apple")
                .setString("Flavour", null)
                .setInt("Qty", 15)
                .setDouble("Price", 26.5)
                .build();

        DataSchema schema = data1.getSchema();

        assertThat(schema.getFieldNames(), contains("Fruit", "Flavour", "Qty", "Price"));

        assertThat(schema.getTypeAt(3), is(int.class));
        assertThat(schema.getTypeNamed("Price"), is(double.class));

        assertThat(IndexedData.equalsIgnoringSchema(data1, ArrayData.of("Apple", null, 15, 26.5)),
                is(true));
    }
}
