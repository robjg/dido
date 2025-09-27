package dido.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dido.data.DidoData;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class ToJsonStringTypeTest {

    @Test
    void gsonDoubleAssumptions() {

        Gson gson = new Gson();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gson.toJson(Double.NaN);
        });

        JsonElement element = new JsonPrimitive(Double.NaN);

        assertThat(gson.toJson(element), is("NaN"));

        ToJsonStringType test = new ToJsonStringType();
        test.setSerializeSpecialFloatingPointValues(true);

        Function<DidoData, String> func = test.toFunction();

        assertThat(func.apply(DidoData.of(Double.NaN)),
                is("{\"f_1\":NaN}"));

        element = new JsonPrimitive(Double.POSITIVE_INFINITY);

        assertThat(gson.toJson(element), is("Infinity"));

        assertThat(func.apply(DidoData.of(Double.POSITIVE_INFINITY)),
                is("{\"f_1\":Infinity}"));

        element = new JsonPrimitive(Double.NEGATIVE_INFINITY);

        assertThat(gson.toJson(element), is("-Infinity"));

        assertThat(func.apply(DidoData.of(Double.NEGATIVE_INFINITY)),
                is("{\"f_1\":-Infinity}"));
    }

    @Test
    void testReadAndWriteNullsAndNans() throws ArooaConversionException, JSONException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("MapJsonNaNsAndNull.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> capture = lookup.lookup("capture.list", List.class);

        assertThat(Double.isNaN(capture.get(2).getDoubleNamed("Price")), is(true));
        assertThat(Double.isInfinite(capture.get(0).getDoubleNamed("Price")), is(true));
        assertThat(capture.get(1).getNamed("Fruit"), nullValue());
        assertThat(capture.get(1).getStringNamed("Fruit"), is("null"));
    }

}