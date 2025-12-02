package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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

        String expected =
                """
                        { "Fruit":"Apple", "Qty":5, "Price":27.2 }
                        { "Fruit":"Orange", "Qty":10, "Price":31.6 }
                        { "Fruit":"Pear", "Qty":7, "Price":22.1 }
                        """;

        JSONAssert.assertEquals(
                results,
                expected,
                JSONCompareMode.LENIENT);
    }

    @Test
    void testReadAndWriteArrayFormatNoSchema() throws ArooaConversionException, JSONException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromToJsonArrayExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        String results = lookup.lookup("results", String.class);

        String expected = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass()
                .getResourceAsStream("/expected/FromToJsonArrayExample.json"))))
                .lines().collect(Collectors.joining());

        JSONAssert.assertEquals(
                results,
                expected,
                JSONCompareMode.LENIENT);
    }

    @Test
    void testReadAndWriteNullsAndNans() throws ArooaConversionException, JSONException, IOException, URISyntaxException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromToJsonNullsAndNans.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<DidoData> capture = lookup.lookup("capture.list", List.class);

        assertThat(Double.isNaN(capture.get(2).getDoubleNamed("Price")), is(true));
        assertThat(Double.isInfinite(capture.get(0).getDoubleNamed("Price")), is(true));
        assertThat(capture.get(1).getNamed("Fruit"), nullValue());
        assertThat(capture.get(1).getStringNamed("Fruit"), is("null"));

        List<String> expectedData = Files.readAllLines(Path.of(Objects.requireNonNull(getClass()
                .getResource("/expected/FromToJsonNullsAndNansData.txt")).toURI()));

        assertThat(capture.stream().map(Objects::toString).toList(), is(expectedData));

        List<String> expectedJson = Files.readAllLines(Path.of(Objects.requireNonNull(getClass()
                .getResource("/expected/FromToJsonNullsAndNans.json")).toURI()));

        assertThat(lookup.lookup("results.lines", List.class), is(expectedJson));
    }

    @Test
    void fromWithGsonBuilder() throws IOException, URISyntaxException {

        Path workDir = Path.of("target/work");
        Files.createDirectories(workDir);

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("FromToWithGsonBuilder.xml")).getFile()));
        Properties properties = new Properties();
        properties.setProperty("work.dir", workDir.toString());
        oddjob.setProperties(properties);

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        List<String> expectedData = Files.readAllLines(Path.of(Objects.requireNonNull(getClass()
                .getResource("/expected/FromToWithGsonBuilderData.txt")).toURI()));

        List<String> actualData = Files.readAllLines(workDir.resolve("FromToWithGsonBuilderData.txt"));

        assertThat(actualData, is(expectedData));

        List<String> expectedJson = Files.readAllLines(Path.of(Objects.requireNonNull(getClass()
                .getResource("/expected/FromToWithGsonBuilder.json")).toURI()));

        List<String> actualJson = Files.readAllLines(workDir.resolve("FromToWithGsonBuilderOut.json"));

        assertThat(actualJson, is(expectedJson));
    }


    public static class IterableOfData implements Iterable<DidoData> {

        final DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .build();

        final List<DidoData> data = DidoData.withSchema(schema)
                .many()
                .of("Apple", Double.POSITIVE_INFINITY)
                .of(null, 31.6)
                .of("Pear", Double.NaN)
                .toList();

        @Override
        public Iterator<DidoData> iterator() {
            return data.iterator();
        }
    }

}
