package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CsvDataOutHowTest {

    @Test
    void testHeaderFromSimpleSchema() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Apple", String.class)
                .addField("Qty", int.class)
                .addField("Price", double.class)
                .build();

        String[] headings = CsvDataOutHow.headerFrom(schema);

        assertThat(headings, is(new String[]{"Apple", "Qty", "Price"}));
    }

    @Test
    void testDataOut() {

        GenericData<String> data = MapData.newBuilderNoSchema()
                .setString("Fruit", "Apple")
                .setInt("Qty", 5)
                .setDouble("Price", 23.5)
                .build();

        Object[] values = CsvDataOutHow.toValues(data);

        assertThat(values, is(new Object[] {"Apple", 5, 23.5}));
    }

    @Test
    void testDataWithHeadings() throws Exception {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Foo", String.class)
                .addField("Quantity", int.class)
                .addField("Price", double.class)
                .build();

        MapData.Values<String> values = MapData.valuesFor(schema);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        DataOut<String> dataOut =
                CsvDataOutHow.withOptions()
                        .schema(schema)
                        .withHeader(true)
                        .make()
                        .outTo(output);

        dataOut.accept(values.of("Apple", null, 5, 19.50));
        dataOut.accept(values.of("Orange", null, 2, 35.24));

        dataOut.close();

        String expected = "Fruit,Foo,Quantity,Price" + System.lineSeparator() +
                "Apple,,5,19.5" + System.lineSeparator() +
                "Orange,,2,35.24" + System.lineSeparator();

        assertThat(output.toString(), is(expected));
    }

}