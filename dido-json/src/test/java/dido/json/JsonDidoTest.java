package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.MapData;
import dido.data.schema.SchemaBuilder;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

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

        DidoData data1 = MapData.builder()
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();
        DidoData data2 = MapData.builder()
                .withString("type", "orange")
                .withInt("qty", 3)
                .withDouble("price", 31.4)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("type", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        JsonDido test = new JsonDido();
        test.setSchema(schema);
        test.setFormat(JsonDidoFormat.ARRAY);

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (DataOut consumer = test.toStreamOut().outTo(results)) {

            consumer.accept(data1);
            consumer.accept(data2);
        }

        JSONAssert.assertEquals(
                results.toString(),
                "[" + JSON_1 + "," + JSON_2 + "]",
                JSONCompareMode.LENIENT);

        List<DidoData> copy = test.toStreamIn().inFrom(
                        new ByteArrayInputStream(results.toByteArray()))
                .stream()
                .collect(Collectors.toList());

        assertThat(copy, contains(data1, data2));
    }

    @Test
    void testToJsonOverrideSchema() {

        DidoData data1 = MapData.builder()
                .withString("type", "apple")
                .withInt("qty", 2)
                .withDouble("price", 26.3)
                .build();
        DidoData data2 = MapData.builder()
                .withString("type", "orange")
                .withInt("qty", 3)
                .withDouble("price", 31.4)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("qty", int.class)
                .build();

        JsonDido test = new JsonDido();

        ByteArrayOutputStream results = new ByteArrayOutputStream();

        try (DataOut consumer = test.toStreamOut().outTo(results)) {

            consumer.accept(data1);
            consumer.accept(data2);
        }

        test.setSchema(schema);
        test.setPartialSchema(true);

        List<DidoData> copy =
                test.toStreamIn().inFrom(
                new ByteArrayInputStream(results.toByteArray()))
                .stream()
                .collect(Collectors.toList());

        assertThat(copy.size(), is(2));



        DidoData copy1 = copy.get(0);
        DidoData copy2 = copy.get(1);



        assertThat(copy1.getStringNamed("type"), is("apple"));
        assertThat(copy1.getIntNamed("qty"), is(2));
        assertThat(copy1.getDoubleNamed("price"), is(26.3));

        assertThat(copy2.getStringNamed("type"), is("orange"));
        assertThat(copy2.getIntNamed("qty"), is(3));
        assertThat(copy2.getDoubleNamed("price"), is(31.4));

        DataSchema schema1 = copy1.getSchema();

        assertThat(schema1.getTypeNamed("type"), is(String.class));
        assertThat(schema1.getTypeNamed("qty"), is(int.class));
        assertThat(schema1.getTypeNamed("price"), is(double.class));

        assertThat(copy2.getSchema(), is(schema1));
    }
}