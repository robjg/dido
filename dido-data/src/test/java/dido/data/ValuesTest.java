package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ValuesTest {

    @Test
    void values() {

        Values<MapData> mapValues = MapData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Qty", int.class)
                        .addNamed("Price", double.class)
                        .build());

        Values<ArrayData> arrayValues = ArrayData.valuesForSchema(
                MapData.schemaBuilder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Qty", int.class)
                        .addNamed("Price", double.class)
                        .build());

        MapData mapData1 = mapValues.of("Apple", 5, 22.4);
        MapData mapData2 = mapValues.of("Pear", 7, 34.2);

        ArrayData arrayData1 = arrayValues.of("Apple", 5, 22.4);
        ArrayData arrayData2 = arrayValues.of("Pear", 7, 34.2);

        assertThat(mapData1, is(arrayData1));
        assertThat(mapData2, is(arrayData2));
    }
}