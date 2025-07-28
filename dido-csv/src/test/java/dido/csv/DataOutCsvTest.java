package dido.csv;

import dido.data.*;
import dido.how.DataOut;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataOutCsvTest {

    @Test
    void testHeaderFromSimpleSchema() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Apple", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        String[] headings = DataOutCsv.headerFrom(schema);

        assertThat(headings, is(new String[]{"Apple", "Qty", "Price"}));
    }

    @Test
    void dataOutNoSchema() {

        DidoData data = MapData.builder()
                .withString("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 23.5)
                .build();

        StringBuilder result = new StringBuilder();

        try (DataOut out = DataOutCsv.with()
                .csvFormat(CSVFormat.DEFAULT.builder()
                        .setRecordSeparator("")
                        .build())
                .toAppendable(result)) {

            out.accept(data);
        }

        assertThat(result.toString(), is("Apple,5,23.5"));
    }

    @Test
    void testDataWithHeadings() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Foo", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        FromValues values = MapData.withSchema(schema);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        DataOut dataOut =
                DataOutCsv.with()
                        .schema(schema)
                        .header(true)
                        .toOutputStream(output);

        dataOut.accept(values.of("Apple", null, 5, 19.50));
        dataOut.accept(values.of("Orange", null, 2, 35.24));

        dataOut.close();

        String expected = "Fruit,Foo,Quantity,Price" + System.lineSeparator() +
                "Apple,,5,19.5" + System.lineSeparator() +
                "Orange,,2,35.24" + System.lineSeparator();

        assertThat(output.toString(), is(expected));
    }

    @Test
    void mapToString() {

        DidoData data = MapData.builder()
                .withString("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 23.5)
                .build();

        String result = DataOutCsv
                .mapToString()
                .apply(data);

        assertThat(result, is("Apple,5,23.5"));

    }
}