package dido.data.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.data.immutable.MapData;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class FieldValuesOutTest {

    @Test
    void valuesOut() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        FieldValuesIn values = FieldValuesIn.withDataFactory(ArrayData.factoryForSchema(dataSchema));

        DidoData data = values.of("Apple", 5, 27.2);

        FieldValuesOut valuesOut = new FieldValuesOut(dataSchema);

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

        Collection<Object> stringCollection = FieldValuesOut.collectionOf(strings);

        String[] aStrings = stringCollection.toArray(new String[0]);

        assertThat(aStrings, is(new String[]{ "Apple", "5", "27.2"}));
    }

    @Test
    void disparateIndices() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamedAt(2,"Fruit", String.class)
                .addNamedAt( 5,"Qty", int.class)
                .addNamedAt( 7, "Price", double.class)
                .build();

        DidoData data = DidoData.withSchema(dataSchema)
                .of("Apple", 5, 27.2);

        FieldValuesOut valuesOut = new FieldValuesOut(dataSchema);

        Collection<Object> collection = valuesOut.toCollection(data);

        assertThat(collection.size(), is(3));
        assertThat(collection.isEmpty(), is(false));

        assertThat(collection.toArray(),
                is(new Object[] { "Apple", 5, 27.2 }));

        Object[] smallArray = new Object[1];
        assertThat(collection.toArray(smallArray),
                is(new Object[] {"Apple", 5, 27.2}));

        Object[] largeArray = new Object[5];
        assertThat(collection.toArray(largeArray),
                is(new Object[] {"Apple", 5, 27.2, null, null}));

        assertThat(collection.toString(), is("[Apple, 5, 27.2]"));

        List<Object> its = new ArrayList<>(collection);
        assertThat(its, contains("Apple", 5, 27.2));

        List<Object> stream= collection.stream().toList();
        assertThat(stream, contains("Apple", 5, 27.2));
    }

    @Test
    void empty() {

        FieldValuesOut valuesOut = new FieldValuesOut(DataSchema.emptySchema());

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
                .collect(FieldValuesIn.withDataFactory(
                        ArrayData.factoryForSchema(DataSchema.emptySchema()))
                        .toCollector());

        assertThat(copy, is(MapData.of()));
        assertThat(copy.hashCode(), is(collection.hashCode()));
    }

    @Test
    void nullValues() {

        DataSchema schema = ArrayData.schemaBuilder()
                .add(String.class)
                .add(int.class)
                .add(double.class)
                .build();

        FieldValuesOut valuesOut = new FieldValuesOut(schema);

        DidoData data = ArrayData.of(null, null, null);

        Collection<Object> collection = valuesOut.toCollection(data);

        assertThat(collection.size(), is(3));
        assertThat(collection.isEmpty(), is(false));

        assertThat(collection.toArray(),
                is(new Object[] { null, null, null }));

        int i = 1;
        for (Object value : collection) {
            assertThat(value, is(data.getAt(i++)));
        }

        List<Object> copy = new ArrayList<>(collection);

        assertThat(copy, is(Arrays.asList(null, null, null)));
    }

    @Test
    void toMap() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        DidoData data = DidoData.withSchema(dataSchema).of("Apple", 5, 27.2);

        Map<String, Object> expected =
                Map.of("Fruit", "Apple", "Qty", 5, "Price", 27.2);

        assertThat(FieldValuesOut.mapOf(data), is(expected));
    }
}
