package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JsonDataPartialCopyTest {

    @Test
    void withNestedRepeatingField() {

        String json = "{ \"OrderId\": \"A123\",\n" +
                "  \"OrderLines\": [\n" +
                "  { \"Fruit\": \"Apple\", \"Qty\": 5 },\n" +
                "  { \"Fruit\": \"Pear\", \"Qty\": 4 }\n" +
                "\t]\n" +
                "\t}\n";

        Gson gson = JsonDataPartialCopy.registerPartialSchema(
                new GsonBuilder(),
                SchemaBuilder.forStringFields().addRepeatingField("OrderLines", DataSchema.emptySchema())
                        .build())
                .create();

        DidoData result = gson.fromJson(json, DidoData.class);

        DataSchema nestedSchema = SchemaBuilder.forStringFields()
                .addField("Fruit", String.class)
                .addField("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.forStringFields()
                .addField("OrderId", String.class)
                .addRepeatingField("OrderLines", nestedSchema)
                .build();

        DidoData expectedData = ArrayData.valuesFor(expectedSchema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 5.0),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 4.0)));

        DataSchema resultSchema = result.getSchema();

        assertThat(resultSchema, is(expectedSchema));

        assertThat(result, is(expectedData));
    }

}