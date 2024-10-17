package dido.csv;

import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamInOutCsvTest {

    @Test
    void testOutAndBackInNoSchema() throws Exception {

        String records = "Fruit,Qty,Price" + System.lineSeparator() +
                "Apple,5,19.50" + System.lineSeparator() +
                "Orange,2,35.24" + System.lineSeparator() +
                "Banana,3,17.65" + System.lineSeparator();

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (DataIn supplier = CsvDataInHow.with()
                    .withHeader(true)
                    .make()
                    .inFrom(new ByteArrayInputStream(records.getBytes(StandardCharsets.UTF_8)));
             DataOut consumer = CsvDataOutHow.with()
                     .withHeader(true)
                     .make()
                     .outTo(results)) {

            for (DidoData data : supplier) {
                consumer.accept(data);
            }
        }

        assertThat(results.toString(), is(records));
    }
}
