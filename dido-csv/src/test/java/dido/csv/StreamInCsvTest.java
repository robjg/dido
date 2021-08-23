package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;
import dido.pickles.CloseableSupplier;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamInCsvTest {

    @Test
    void testWithSchema() throws IOException {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Type", String.class)
                .addField("Quantity", int.class)
                .addField("Price", double.class)
                .build();

        StreamInCsv<String> test = new StreamInCsv<>(schema);


        CloseableSupplier<GenericData<String>> supplier = test.supplierFor(new ByteArrayInputStream("Apple,5,27.2".getBytes()));

        GenericData<String> data = supplier.get();

        assertThat(data.getString("Type"), is("Apple"));
        assertThat(data.getInt("Quantity"), is(5));
        assertThat(data.getDouble("Price"), is(27.2));
    }

}