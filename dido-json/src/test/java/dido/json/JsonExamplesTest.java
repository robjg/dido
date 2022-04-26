package dido.json;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonExamplesTest {

    @Test
    void testReadAndWriteNoSchema() throws ArooaConversionException, JSONException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromToJsonExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        String results = lookup.lookup("results", String.class);

        String expected = "[\n" +
                "    { \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 },\n" +
                "    { \"Fruit\"=\"Orange\", \"Qty\"=10, \"Price\"=31.6 },\n" +
                "    { \"Fruit\"=\"Pear\", \"Qty\"=7, \"Price\"=22.1 }\n" +
                "]\n";

        JSONAssert.assertEquals(
                results,
                expected,
                JSONCompareMode.LENIENT);
    }
}
