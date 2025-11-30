package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.ToNumberPolicy;
import dido.data.DidoData;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JsonStringToDataTest {

    @Test
    void testLines() {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .setStrictness(Strictness.LEGACY_STRICT);

        Function<String, DidoData> func = JsonStringToData.asCopy().make(gsonBuilder);

        DidoData data = func.apply("{ \"Fruit\":\"Apple\", \"Qty\":5, \"Price\":27.2 }");

        assertThat(data, is(DidoData.of("Apple", 5.0, 27.2)));
    }

    @Test
    void objectToNumberLong() {

        Function<String, DidoData> func = DataInJson.with()
                .gsonBuilder(gson -> gson.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE))
                .mapFromString();

        DidoData data = func.apply("{ \"Fruit\":\"Apple\", \"Qty\":5, \"Price\":27.2 }");

        assertThat(data.getSchema().getTypeNamed("Qty"), is(long.class));

        assertThat(data, is(DidoData.of("Apple", 5L, 27.2)));
    }
}