package dido.json;

import dido.data.GenericData;
import dido.data.MapData;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;
import org.oddjob.io.BufferType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamOutJsonLinesTest {

    @Test
    void testSimpleOut() throws Exception {

        GenericData<String> data1 = MapData.of(
                "Fruit", "Apple", "Qty", 5, "Price", 27.2);
        GenericData<String> data2 = MapData.of(
                "Fruit", "Orange", "Qty", 10, "Price", 31.6);
        GenericData<String> data3 = MapData.of(
                "Fruit", "Pear", "Qty", 7, "Price", 22.1);

        BufferType bufferType = new BufferType();
        bufferType.configured();

        DataOut<String> dataOut = new StreamOutJsonLines().outTo(bufferType.toOutputStream());

        dataOut.accept(data1);
        dataOut.accept(data2);
        dataOut.accept(data3);

        dataOut.close();

        String[] lines = bufferType.getLines();

        assertThat(lines.length, is(3));
        assertThat(lines[0], is("{\"Fruit\":\"Apple\",\"Qty\":5,\"Price\":27.2}"));
        assertThat(lines[1], is("{\"Fruit\":\"Orange\",\"Qty\":10,\"Price\":31.6}"));
        assertThat(lines[2], is("{\"Fruit\":\"Pear\",\"Qty\":7,\"Price\":22.1}"));
    }
}