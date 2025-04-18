package dido.json;

import dido.data.DidoData;
import dido.data.MapData;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataOutJsonLinesTest {

    @Test
    void testSimpleOut() {

        DidoData data1 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 27.2);
        DidoData data2 = MapData.of(
                "Fruit", "Orange", "Qty", 10, "Price", Double.NaN);
        DidoData data3 = MapData.of(
                "Fruit", "Pear", "Qty", 7, "Price", 22.1);

        StringBuilder stringBuilder = new StringBuilder();

        try (DataOut dataOut = DataOutJson.with()
                .serializeSpecialFloatingPointValues()
                .outFormat(JsonDidoFormat.LINES)
                .toAppendable(stringBuilder)) {

            dataOut.accept(data1);
            dataOut.accept(data2);
            dataOut.accept(data3);
        }

        String[] lines = stringBuilder.toString().split("\n");

        assertThat(lines.length, is(3));
        assertThat(lines[0], is("{\"Fruit\":\"Apple\",\"Qty\":5,\"Price\":27.2}"));
        assertThat(lines[1], is("{\"Fruit\":\"Orange\",\"Qty\":10,\"Price\":NaN}"));
        assertThat(lines[2], is("{\"Fruit\":\"Pear\",\"Qty\":7,\"Price\":22.1}"));
    }
}