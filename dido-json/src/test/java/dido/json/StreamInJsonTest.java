package dido.json;

import dido.data.*;
import dido.how.DataIn;
import dido.how.DataInHow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class StreamInJsonTest {

    @Test
    void readsArrayOk() throws Exception {

        String json = "[\n" +
                "    { \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 },\n" +
                "    { \"Fruit\"=\"Orange\", \"Qty\"=10, \"Price\"=31.6 },\n" +
                "    { \"Fruit\"=\"Pear\", \"Qty\"=7, \"Price\"=22.1 }\n" +
                "]\n";

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addField("Fruit", String.class)
                .addField("Qty", Double.class)
                .addField("Price", Double.class)
                .build();

        ArrayData.Builder expectedBuilder = ArrayData.builderForSchema(expectedSchema);

        DataInHow<InputStream, NamedData> test = StreamInJson.settings()
                .setIsArray(true)
                .make();

        try (DataIn<NamedData> in = test.inFrom(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))) {

            DidoData data1 = in.get();

            assertThat(data1.getSchema(), is(expectedSchema));

            assertThat(data1, is(expectedBuilder.build("Apple", 5.0, 27.2)));

            DidoData data2 = in.get();

            assertThat(data2, is(expectedBuilder.build("Orange", 10.0, 31.6)));

            DidoData data3 = in.get();

            assertThat(data3, is(expectedBuilder.build("Pear", 7.0, 22.1)));

            assertThat(in.get(), nullValue());
        }
    }

    @Test
    void readsNestedArray() throws Exception {

        String json = "[\n" +
                "  { \"OrderId\": \"A123\", \n" +
                "    \"OrderLines\": [ \n" +
                "      {\"Fruit\": \"Apple\", \"Qty\": 4}, \n" +
                "      {\"Fruit\": \"Pear\", \"Qty\": 5}\n" +
                "    ]\n" +
                "  }\n" +
                "]";

        DataSchema schema = SchemaBuilder.newInstance()
                .addRepeatingField("OrderLines", DataSchema.emptySchema())
                .build();

        DataInHow<InputStream, NamedData> test = StreamInJson.settings()
                .setIsArray(true)
                .setSchema(schema)
                .setPartial(true)
                .make();

        DataSchema expectedNestedSchema = SchemaBuilder.newInstance()
                .addField("Fruit", String.class)
                .addField("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", expectedNestedSchema)
                .build();

        try (DataIn<NamedData> in = test.inFrom(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))) {

            NamedData data1 = in.get();

            assertThat(data1.getSchema(), is(expectedSchema));

            RepeatingData repeatingData = (RepeatingData) data1.get("OrderLines");

            assertThat(repeatingData.size(), is(2));

            DidoData orderLine1 = repeatingData.get(0);
            assertThat(orderLine1.getAt(1), is("Apple"));
            assertThat(orderLine1.getAt(2), is(4.0));
        }
    }
}