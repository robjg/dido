package dido.data;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class MapRecordTest {

    @Test
    void testNoSchema() throws ParseException {

        DataBuilder<String> builder = MapRecord.newBuilderNoSchema();

        GenericData<String> data1 = builder
                .setString("type", "apple")
                .setInt("qty", 2)
                .setDouble("price", 26.3)
                .setObject("date", new SimpleDateFormat("yyyy-MM-dd").parse("2021-09-22"))
                .build();

        assertThat(data1.getString("type"), is("apple"));
        assertThat(data1.getInt("qty"), is(2));
        assertThat(data1.getDouble("price"), is(26.3));

        DataSchema<String> schema1 = data1.getSchema();

        assertThat(schema1.getType("type"), is(String.class));
        assertThat(schema1.getType("qty"), is(int.class));
        assertThat(schema1.getType("price"), is(double.class));
        assertThat(schema1.getType("date"), is(Date.class));

        GenericData<String> data2 = builder
                .setString("type", "apple")
                .setLong("qty", 2)
                .setFloat("price", 26.3F)
                .setBoolean("offer", true)
                .build();

        assertThat(data2.getString("type"), is("apple"));
        assertThat(data2.getLong("qty"), is(2L));
        assertThat((double) data2.getFloat("price"), closeTo(26.3, 0.01));
        assertThat(data2.getBoolean("offer"), is(true));

        DataSchema<String> schema2 = data2.getSchema();

        assertThat(schema2.getType("type"), is(String.class));
        assertThat(schema2.getType("qty"), is(long.class));
        assertThat(schema2.getType("price"), is(float.class));
        assertThat(schema2.getType("offer"), is(boolean.class));
    }

}