package dido.json;

import dido.data.*;
import dido.how.DataIn;
import dido.how.DataInHow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StreamInJsonTest {

    @Test
    void readsArrayOk() throws Exception {

        String json = "[\n" +
                "    { \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 },\n" +
                "    { \"Fruit\"=\"Orange\", \"Qty\"=10, \"Price\"=31.6 },\n" +
                "    { \"Fruit\"=\"Pear\", \"Qty\"=7, \"Price\"=22.1 }\n" +
                "]\n";

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .addNamed("Price", Double.class)
                .build();

        Values<ArrayData> values = ArrayData.valuesForSchema(expectedSchema);

        DataInHow<InputStream> test = StreamInJson.asCopy()
                .setIsArray(true)
                .make();

        try (DataIn in = test.inFrom(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))) {

            Iterator<DidoData> it = in.iterator();

            assertThat("Has next", it.hasNext());
            DidoData data1 = it.next();

            assertThat(data1.getSchema(), is(expectedSchema));

            assertThat(data1, is(values.of("Apple", 5.0, 27.2)));

            assertThat("Has next", it.hasNext());
            DidoData data2 = it.next();

            assertThat(data2, is(values.of("Orange", 10.0, 31.6)));

            assertThat("Has next", it.hasNext());
            DidoData data3 = it.next();

            assertThat(data3, is(values.of("Pear", 7.0, 22.1)));

            assertThat("!Has next", !it.hasNext());
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
                .addRepeatingNamed("OrderLines", DataSchema.emptySchema())
                .build();

        DataInHow<InputStream> test = StreamInJson.asCopy()
                .setIsArray(true)
                .setSchema(schema)
                .setPartial(true)
                .make();

        DataSchema expectedNestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", expectedNestedSchema)
                .build();

        try (DataIn in = test.inFrom(
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))) {

            Iterator<DidoData> it = in.iterator();

            assertThat("Has next", it.hasNext());
            DidoData data1 = it.next();

            assertThat(data1.getSchema(), is(expectedSchema));

            RepeatingData repeatingData = (RepeatingData) data1.getNamed("OrderLines");

            assertThat(repeatingData.size(), is(2));

            DidoData orderLine1 = repeatingData.get(0);
            assertThat(orderLine1.getAt(1), is("Apple"));
            assertThat(orderLine1.getAt(2), is(4.0));

            assertThat("!Has next", !it.hasNext());
        }
    }
}