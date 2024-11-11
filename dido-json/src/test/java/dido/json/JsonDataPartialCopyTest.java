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

        DataFactoryProvider dataFactoryProvider = new ArrayDataDataFactoryProvider();

        Gson gson = JsonDataPartialCopy.registerPartialSchema(
                        new GsonBuilder(),
                        SchemaBuilder.newInstance()
                                .addRepeatingNamed("OrderLines", DataSchema.emptySchema())
                                .build(), dataFactoryProvider)
                .create();

        DidoData result = gson.fromJson(json, DidoData.class);

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .build();

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("OrderId", String.class)
                .addRepeatingNamed("OrderLines", nestedSchema)
                .build();

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesForSchema(nestedSchema)
                                        .of("Apple", 5.0),
                                ArrayData.valuesForSchema(nestedSchema)
                                        .of("Pear", 4.0)));

        DataSchema resultSchema = result.getSchema();

        assertThat(resultSchema, is(expectedSchema));

        assertThat(result, is(expectedData));
    }

}