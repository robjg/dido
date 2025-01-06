package dido.json;

import com.google.gson.GsonBuilder;
import dido.data.*;
import dido.data.util.FieldValuesIn;
import dido.how.DataIn;
import dido.how.DataInHow;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class DataInJsonReaderTest {

    @Test
    void readsArrayOk() {

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

        FieldValuesIn values = ArrayData.valuesWithSchema(expectedSchema);

        DataInHow<Reader> test = DataInJsonReader.asCopy()
                .setIsArray(true)
                .make(new GsonBuilder());

        try (DataIn in = test.inFrom(new StringReader(json))) {

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
    void readsNestedArray() {

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

        DataInHow<Reader> test = DataInJsonReader.asCopy()
                .setIsArray(true)
                .setSchema(schema)
                .setPartial(true)
                .make(new GsonBuilder());

        DataSchema expectedNestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", expectedNestedSchema)
                .build();

        try (DataIn in = test.inFrom(new StringReader(json))) {

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

    @Test
    void readsObjects() {

        String json =
                "{ \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 }" +
                        "{ \"Fruit\"=\"Orange\", \"Qty\"=10, \"Price\"=31.6 }" +
                        "{ \"Fruit\"=\"Pear\", \"Qty\"=7, \"Price\"=22.1 }";

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .addNamed("Price", Double.class)
                .build();

        FieldValuesIn values = ArrayData.valuesWithSchema(expectedSchema);

        DataInHow<Reader> test = DataInJsonReader.asCopy()
                .make(new GsonBuilder());

        try (DataIn in = test.inFrom(new StringReader(json))) {

            List<DidoData> results = in.stream()
                    .collect(Collectors.toList());

            assertThat(results, contains(
                    values.of("Apple", 5.0, 27.2),
                    values.of("Orange", 10.0, 31.6),
                    values.of("Pear", 7.0, 22.1)));
        }
    }

}