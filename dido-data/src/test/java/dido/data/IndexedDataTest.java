package dido.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IndexedDataTest {

    static class MyData extends AbstractIndexedData<Void> {

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
    void testToString() {

        GenericData<String> data1 = ArrayData.of("Apple", null, 15, 26.5);

        assertThat(IndexedData.toString(data1),
                is("{[1]=Apple, [2]=null, [3]=15, [4]=26.5}"));
    }

    @Test
    void testEquals() {

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Qty", Double.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", nestedSchema)
                .build();

        IndexedData<String> data1 = ArrayData.valuesFor(schema)
                .of("A123",
                        List.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 5)));

        IndexedData<String> data2 = ArrayData.valuesFor(schema)
                .of("A123",
                        List.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 4),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 5)));

        assertThat(IndexedData.equals(data1, data2), is(true));
        assertThat(IndexedData.hashCode(data1), is(IndexedData.hashCode(data2)));
    }

    @Test
    void testEqualsIgnoringSchema() {

        IndexedData<String> data1 = ArrayData.of("Apple", 5, 23.7);
        IndexedData<String> data2 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 23.7);

        assertThat(IndexedData.equalsIgnoringSchema(data1, data2), is(true));
    }
}