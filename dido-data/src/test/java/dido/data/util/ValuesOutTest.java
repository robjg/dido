package dido.data.util;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ValuesOutTest {


    @Test
    void valuesOut() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        Values<ArrayData> values = Values.withDataFactory(ArrayData.factoryForSchema(dataSchema));

        ArrayData data = values.of("Apple", 5, 27.2);

        ValuesOut valuesOut = new ValuesOut(dataSchema);

        Collection<Object> collection = valuesOut.toCollection(data);

        assertThat(collection.size(), is(3));
        assertThat(collection.isEmpty(), is(false));

        assertThat(collection.toArray(),
                is(new Object[] { "Apple", 5, 27.2 }));

        assertThat(collection.toString(), is("[Apple, 5, 27.2]"));

        int i = 1;
        for (Object value : collection) {
            assertThat(value, is(data.getAt(i++)));
        }

        DidoData copy = collection.stream().collect(values.toCollector());

        assertThat(copy, is(data));

        DidoData strings = collection.stream()
                .map(Object::toString)
                .collect(values.toCollector());

        Collection<Object> stringCollection = ValuesOut.collectionOf(strings);

        String[] aStrings = stringCollection.toArray(new String[0]);

        assertThat(aStrings, is(new String[]{ "Apple", "5", "27.2"}));
    }

    @Test
    void empty() {

        ValuesOut valuesOut = new ValuesOut(DataSchema.emptySchema());

        Collection<Object> collection = valuesOut.toCollection(ArrayData.of());

        assertThat(collection.size(), is(0));
        assertThat(collection.isEmpty(), is(true));

        assertThat(collection.toArray(),
                is(new Object[] {  }));

        for (Object ignored : collection) {
            throw new AssertionError("Shouldn't be here");
        }

        @SuppressWarnings("RedundantOperationOnEmptyContainer")
        DidoData copy = collection.stream()
                .collect(Values.withDataFactory(
                        ArrayData.factoryForSchema(DataSchema.emptySchema()))
                        .toCollector());

        assertThat(copy, is(MapData.of()));
        assertThat(copy.hashCode(), is(collection.hashCode()));
    }
}
