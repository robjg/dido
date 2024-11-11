package dido.data.util;

import dido.data.ArrayData;
import dido.data.DidoData;
import dido.data.MapData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class FieldValuesInTest {

    @Test
    void values() {

        FieldValuesIn mapValues = MapData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Qty", int.class)
                        .addNamed("Price", double.class)
                        .build());

        FieldValuesIn arrayValues = ArrayData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Qty", int.class)
                        .addNamed("Price", double.class)
                        .build());

        DidoData mapData1 = mapValues.of("Apple", 5, 22.4);
        DidoData mapData2 = mapValues.of("Pear", 7, 34.2);

        DidoData arrayData1 = arrayValues.of("Apple", 5, 22.4);
        DidoData arrayData2 = arrayValues.of("Pear", 7, 34.2);

        assertThat(mapData1, is(arrayData1));
        assertThat(mapData2, is(arrayData2));
    }

    @Test
    void copy() {

        FieldValuesIn arrayValues = ArrayData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Qty", int.class)
                        .addNamed("Price", double.class)
                        .build());

        DidoData arrayData1 = arrayValues.copy(MapData.of("Fruit", "Apple",
                "Qty", 5, "Price", 22.4));

        assertThat(arrayData1, is(arrayValues.of("Apple", 5, 22.4)));
    }

    @Test
    void collectionWithNulls() {

        FieldValuesIn arrayValues = ArrayData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Colour", String.class)
                        .build());

        ArrayList<Object> list = new ArrayList<>();
        list.add(null);
        list.add("Red");

        DidoData arrayData = arrayValues.ofCollection(list);

        assertThat(arrayData.getAt(1), nullValue());
        assertThat(arrayData.getAt(2), is("Red"));
    }
}