package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ArrayDataTest {

    @Test
    void testEmptyData() {

        GenericData<String> data = ArrayData.of();

        DataSchema<String> schema = data.getSchema();

        assertThat(schema.firstIndex(), is(0));
        assertThat(schema.lastIndex(), is(0));
    }

    @Test
    void testSimpleData() {

        GenericData<String> data = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data.getStringAt(1), is("Apple"));
        assertThat(data.hasIndex(2), is(false));
        assertThat(data.getIntAt(3), is(15));
        assertThat(data.getDoubleAt(4), is(26.5));

        DataSchema<String> schema = data.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(0));
    }

    @Test
    void testToString() {

        GenericData<String> data1 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1.toString(), is("[Apple, null, 15, 26.5]"));
    }

    @Test
    void testEqualsAndHashCode() {

        GenericData<String> data1 = ArrayData.of("Apple", null, 15, 26.5);

        GenericData<String> data2 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(data1, is(data2));
        assertThat(data1.hashCode(), is(data2.hashCode()));
    }
}
