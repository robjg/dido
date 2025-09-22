package dido.data.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class SubDataTest {

    @Test
    void byFields() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Id", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Colour", String.class)
                .build();

        DidoData data = DidoData.withSchema(dataSchema).of("F1", "Apple", 5, 23.4, "Red");

        DidoData subData = SubData.of(data).withNames("Id", "Colour");

        assertThat(subData, is(DidoData.of("F1", "Red")));
    }


    @Test
    void asMappingFunc() {

        DataSchema dataSchema = ArrayData.schemaBuilder()
                .addNamed("Id", String.class)
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("Colour", String.class)
                .build();

        List<DidoData> data = DidoData.withSchema(dataSchema).many()
                .of("F1", "Apple", 5, 23.4, "Red")
                .of("F2", "Orange", 3, 57.7, "Orange")
                .of("F3", "Banana", 2, 34.2, "Yellow")
                .toList();

        List<DidoData> subData = data.stream()
                .map(SubData.asMappingFrom(dataSchema).withNames("Fruit"))
                .toList();

        assertThat(subData, contains(
                DidoData.of("Apple"), DidoData.of("Orange"), DidoData.of("Banana")));
    }

    @Test
    void fieldsOut() {

        DataSchema schema = ArrayData.schemaBuilder()
                .add(String.class)
                .add(int.class)
                .add(double.class)
                .build();

        DidoData data = ArrayData.withSchema(schema).of("Apple", 5, 27.2);

        DidoData subData = SubData.of(data).withIndices(2);

        FieldValuesOut valuesOut = new FieldValuesOut(subData.getSchema());
        Collection<Object> collection = valuesOut.toCollection(subData);

        assertThat(collection.toArray(),
                is(new Object[] { 5 }));
    }

}