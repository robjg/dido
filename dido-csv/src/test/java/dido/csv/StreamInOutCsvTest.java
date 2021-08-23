package dido.csv;

import dido.data.GenericData;
import org.junit.jupiter.api.Test;
import dido.pickles.CloseableConsumer;
import dido.pickles.CloseableSupplier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamInOutCsvTest {

    @Test
    void testOutAndBackInNoSchema() throws IOException {

        String records = "Fruit,Qty,Price" + System.lineSeparator() +
                "Apple,5,19.50" + System.lineSeparator() +
                "Orange,2,35.24" + System.lineSeparator() +
                "Banana,3,17.65" + System.lineSeparator();

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (CloseableSupplier<GenericData<String>> supplier = new StreamInCsv<String>(true)
                .supplierFor(new ByteArrayInputStream(records.getBytes(StandardCharsets.UTF_8)));
             CloseableConsumer<GenericData<String>> consumer = new StreamOutCsv<String>(true)
                     .consumerFor(results)) {

            while (true) {
                GenericData<String> data = supplier.get();
                if (data == null) {
                    break;
                }
                consumer.accept(data);
            }
        }

        assertThat(results.toString(), is(records));
    }
}
