package dido.data;

import dido.data.generic.GenericDataSchema;
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
        public GenericDataSchema<Void> getSchema() {
            throw new UnsupportedOperationException();
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
    void testIntTypeCastAssumptions() {

        MyData data = new MyData(42);

        assertThat(data.getIntAt(1), is(42));

        // This isn't allowed.
        try {
            data.getLongAt(1);
            assertThat("Integer can't be cast to Long.", false);
        }
        catch (ClassCastException e) {
            // expected.
        }

        // This is allowed though
        assertThat((long) data.getIntAt(1), is(42L));
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