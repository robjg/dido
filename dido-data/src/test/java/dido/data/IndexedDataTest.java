package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IndexedDataTest {

    static class MyData implements IndexedData<Void> {

        private final Object data;

        MyData(Object data) {
            this.data = data;
        }

        @Override
        public DataSchema<Void> getSchema() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getAt(int index) {
            return data;
        }

        @Override
        public <T> T getAtAs(int index, Class<T> type) {
            //noinspection unchecked
            return (T) data;
        }

        @Override
        public boolean hasIndex(int index) {
            return false;
        }
    }

    @Test
    void testInt() {

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
    void testToString() {

        GenericData<String> data1 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(IndexedData.toString(data1),
                is("{[1]=Apple, [2]=null, [3]=15, [4]=26.5}"));
    }
}