package dido.json;

import com.google.gson.Gson;
import com.google.gson.Strictness;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.FromValues;
import dido.data.immutable.ArrayData;
import dido.data.schema.SchemaBuilder;
import dido.how.DataIn;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DataInJsonTest {

    @Test
    void simplestRead() {

        String json =
                "{ \"Fruit\":\"Apple\", \"Qty\":5, \"Price\":27.2 }";

        DataSchema expectedSchema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .addNamed("Price", Double.class)
                .build();

        FromValues values = ArrayData.withSchema(expectedSchema);

        try (DataIn in = DataInJson.fromReader(new StringReader(json))) {

            List<DidoData> results = in.stream()
                    .collect(Collectors.toList());

            assertThat(results, contains(
                    values.of("Apple", 5.0, 27.2)));
        }
    }

    @Test
    void readSpecialNumericValue() {

        Gson gson = new Gson();

        double infinity = gson.fromJson("Infinity", double.class);

        assertThat(Double.isInfinite(infinity), is(true));

        var map = gson.fromJson("{\"Price\"=Infinity}", Map.class);

        // Not that by default this is a string
        assertThat(map.get("Price"), instanceOf(String.class));

        String json =
                "{ \"Price\"=Infinity }" +
                        "{ \"Price\"=NaN }";

        try (DataIn in = DataInJson.with()
                .strictness(Strictness.LENIENT)
                .fromReader(new StringReader(json))) {

            List<DidoData> results = in.stream().toList();

            // Without a schema this doesn't work - treated as strings
            assertThat(results.get(0).getNamed("Price"), is("Infinity"));
            assertThat(results.get(1).getNamed("Price"), is("NaN"));
        }

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Price", double.class)
                .build();

        try (DataIn in = DataInJson.with()
                .schema(schema)
                .strictness(Strictness.LENIENT)
                .fromReader(new StringReader(json))) {

            List<DidoData> results = in.stream().toList();

            assertThat(Double.isInfinite(results.get(0).getDoubleNamed("Price")),
                    is(true));
            assertThat(Double.isNaN(results.get(1).getDoubleNamed("Price")),
                    is(true));
        }
    }

    @Test
    void readNulls() {

        Gson gson = new Gson();

        String aNull = gson.fromJson("null", String.class);

        assertThat(aNull, nullValue());

        var map = gson.fromJson("{\"Fruit\"=null}", Map.class);

        // Not that by default this is a string
        assertThat(map.get("Fruit"), nullValue());

        String json =
                "{ \"Fruit\"=null }";

        DidoData dataNoSchema = DataInJson
                .mapFromString().apply(json);

        assertThat(dataNoSchema.getNamed("Fruit"), nullValue());

        assertThat(dataNoSchema.getSchema().getTypeNamed("Fruit"), is(void.class));

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("Fruit", String.class)
                .build();

        DidoData dataWithSchema = DataInJson.with()
                .schema(schema)
                .mapFromString().apply(json);

        assertThat(dataWithSchema.getNamed("Fruit"), nullValue());
    }
}