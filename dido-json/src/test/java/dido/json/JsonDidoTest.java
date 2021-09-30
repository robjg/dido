package dido.json;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import dido.pickles.CloseableSupplier;
import dido.pickles.DataIn;
import dido.pickles.DataOut;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class JsonDidoTest {

    static final String JSON_1 = "{" +
            "    \"type\": \"apple\",\n" +
            "    \"qty\": 2,\n" +
            "    \"price\": 26.3\n" +
            "}";
    static final String JSON_2 = "{" +
            "    \"type\": \"orange\",\n" +
            "    \"qty\": 3,\n" +
            "    \"price\": 31.4\n" +
            "}";

    @Test
    void testToJsonAndBackFixedSchema() throws Exception {

        GenericData<String> data1 = MapData.newBuilderNoSchema()
                .setString("type", "apple")
                .setInt("qty", 2)
                .setDouble("price", 26.3)
                .build();
        GenericData<String> data2 = MapData.newBuilderNoSchema()
                .setString("type", "orange")
                .setInt("qty", 3)
                .setDouble("price", 31.4)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("type", String.class)
                .addField("qty", int.class)
                .addField("price", double.class)
                .build();

        JsonDido test = new JsonDido();
        test.setSchema(schema);

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (DataOut<String> consumer = test.toStreamOut().outTo(results)) {

            consumer.accept(data1);
            consumer.accept(data2);
        }

        JSONAssert.assertEquals(
                results.toString(),
                "[" + JSON_1 + "," + JSON_2 + "]",
                JSONCompareMode.LENIENT);


        List<GenericData<String>> copy = new ArrayList<>();

        try (CloseableSupplier<GenericData<String>> supplier = test.toStreamIn().inFrom(
                new ByteArrayInputStream(results.toByteArray()))) {

            while (true) {
                GenericData<String> data = supplier.get();
                if (data == null) {
                    break;
                }
                copy.add(data);
            }
        }

        assertThat(copy, contains(data1, data2));
    }

    @Test
    void testToJsonOverrideSchema() throws Exception {

        GenericData<String> data1 = MapData.newBuilderNoSchema()
                .setString("type", "apple")
                .setInt("qty", 2)
                .setDouble("price", 26.3)
                .build();
        GenericData<String> data2 = MapData.newBuilderNoSchema()
                .setString("type", "orange")
                .setInt("qty", 3)
                .setDouble("price", 31.4)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("qty", int.class)
                .build();

        JsonDido test = new JsonDido();
        test.setSchema(schema);
        test.setPartialSchema(true);

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (DataOut<String> consumer = test.toStreamOut().outTo(results)) {

            consumer.accept(data1);
            consumer.accept(data2);
        }

        List<GenericData<String>> copy = new ArrayList<>();

        try (DataIn<String> supplier = test.toStreamIn().inFrom(
                new ByteArrayInputStream(results.toByteArray()))) {

            while (true) {
                GenericData<String> data = supplier.get();
                if (data == null) {
                    break;
                }
                copy.add(data);
            }
        }

        assertThat(copy.size(), is(2));

        GenericData<String> copy1 = copy.get(0);
        GenericData<String> copy2 = copy.get(1);

        assertThat(copy1.getString("type"), is("apple"));
        assertThat(copy1.getInt("qty"), is(2));
        assertThat(copy1.getDouble("price"), is(26.3));

        assertThat(copy2.getString("type"), is("orange"));
        assertThat(copy2.getInt("qty"), is(3));
        assertThat(copy2.getDouble("price"), is(31.4));

        DataSchema<String> schema1 = copy1.getSchema();

        assertThat(schema1.getType("type"), is(String.class));
        assertThat(schema1.getType("qty"), is(int.class));
        assertThat(schema1.getType("price"), is(double.class));

        assertThat(copy2.getSchema(), is(schema1));
    }
}