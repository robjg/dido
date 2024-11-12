package dido.json;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.SchemaBuilder;
import dido.data.util.FieldValuesIn;
import dido.how.DataIn;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

class DataInJsonTest {

    @Test
    void simplestRead() {

        String json =
                "{ \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 }" +
                        "{ \"Fruit\"=\"Orange\", \"Qty\"=10, \"Price\"=31.6 }" +
                        "{ \"Fruit\"=\"Pear\", \"Qty\"=7, \"Price\"=22.1 }";

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .addNamed("Price", Double.class)
                .build();

        FieldValuesIn values = ArrayData.valuesForSchema(expectedSchema);

        try (DataIn in = DataInJson.fromReader(new StringReader(json))) {

            List<DidoData> results = in.stream()
                    .collect(Collectors.toList());

            assertThat(results, contains(
                    values.of("Apple", 5.0, 27.2),
                    values.of("Orange", 10.0, 31.6),
                    values.of("Pear", 7.0, 22.1)));
        }

    }
}