package dido.data;

import dido.data.useful.AbstractIndexedData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IndexedDataTest {

    static class MyData extends AbstractIndexedData {

        private final Object data;

        MyData(Object data) {
            this.data = data;
        }

        @Override
        public IndexedSchema getSchema() {
            throw new UnsupportedOperationException("No being tested here");
        }

        @Override
        public Object getAt(int index) {
            return data;
        }

        @Override
        public boolean hasIndex(int index) {
            return false;
        }
    }

    @Test
    void testIntAsOtherNumericTypes() {

        MyData data = new MyData(42);

        assertThat(data.getAt(1), is( 42));
        assertThat(data.getByteAt(1), is((byte) 42));
        assertThat(data.getShortAt(1), is((short) 42));
        assertThat(data.getIntAt(1), is(42));
        assertThat(data.getIntAt(1), is(42));
        assertThat(data.getLongAt(1), is(42L));
        assertThat(data.getFloatAt(1), is(42.0F));
        assertThat(data.getDoubleAt(1), is(42.0));
        assertThat(data.getStringAt(1), is("42"));
    }

    @Test
    void testDoubleAsOtherNumericTypes() {

        MyData data = new MyData(42.0);

        assertThat(data.getAt(1), is( 42.0));
        assertThat(data.getByteAt(1), is((byte) 42));
        assertThat(data.getShortAt(1), is((short) 42));
        assertThat(data.getIntAt(1), is(42));
        assertThat(data.getIntAt(1), is(42));
        assertThat(data.getLongAt(1), is(42L));
        assertThat(data.getFloatAt(1), is(42.0F));
        assertThat(data.getDoubleAt(1), is(42.0));
        assertThat(data.getStringAt(1), is("42.0"));
    }

    @Test
    void toStringEtc() {

        MyData data1 = new MyData(42);

        try {
            String expected = data1.toString();

            assertThat("Should Fail, not " + expected,false);
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }


    @Test
    void testEqualsIgnoringSchema() {

        IndexedData data1 = ArrayData.of("Apple", 5, 23.7);
        IndexedData data2 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 23.7);

        assertThat(IndexedData.equalsIgnoringSchema(data1, data2), is(true));
    }
}