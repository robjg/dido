package dido.csv;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapRecord;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamOutCsvTest {

    @Test
    void testHeaderFromSimpleSchema() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("Apple", String.class)
                .addField("Qty", int.class)
                .addField("Price", double.class)
                .build();

        String[] headers = StreamOutCsv.headers(schema);

        assertThat(headers, is(new String[]{"Apple", "Qty", "Price"}));
    }

    @Test
    void testDataOut() {

        GenericData<String> data = MapRecord.newBuilderNoSchema()
                .setString("Fruit", "Apple")
                .setInt("Qty", 5)
                .setDouble("Price", 23.5)
                .build();

        Object[] values = StreamOutCsv.toValues(data);

        assertThat(values, is(new Object[] {"Apple", 5, 23.5}));
    }
}