package dido.examples;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.tools.ConsoleCapture;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OddballExamplesTest {

    @Test
    void firstExamples() throws IOException, JSONException {

        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitNoHeader.csv"));
             OutputStream out = Files.newOutputStream(Path.of("Fruit.csv"))) {

            in.transferTo(out);
        }

        ConsoleCapture consoleCapture1 = new ConsoleCapture();
        try (ConsoleCapture.Close ignore = consoleCapture1.captureConsole()) {

            Oddjob oddjob = captureOddjob1();
            assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
        }
        String result1 = consoleCapture1.getAll();

        String expected1 = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitAllText.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected1, result1, JSONCompareMode.LENIENT);

        ConsoleCapture consoleCapture2 = new ConsoleCapture();
        try (ConsoleCapture.Close ignore = consoleCapture2.captureConsole()) {

            Oddjob oddjob = captureOddjob2();
            assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));
        }
        String result2 = consoleCapture2.getAll();

        String expected2 = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitLines.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected2, result2, JSONCompareMode.LENIENT);

        Files.delete(Path.of("Fruit.csv"));
    }

    Oddjob captureOddjob1() {

        // #oddjobCode{
        File config = new File(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("examples/CsvToJson.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(config);

        oddjob.run();
        // }#oddjobCode

        return oddjob;
    }

    Oddjob captureOddjob2() {

        File config = new File(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("examples/CsvToJsonWithSchema.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(config);

        oddjob.run();

        return oddjob;
    }

    @Test
    void transformWithSchema() throws JSONException, IOException {

        File file = new File(Objects.requireNonNull(getClass().getResource(
                "/examples/JsonTransformWithSchema.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {

            oddjob.run();
        }

        assertTrue(oddjob.lastStateEvent().getState().isComplete());

        String actual = console.getAll();

        String expected = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitWithTransformation.jsonl")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);

        oddjob.destroy();
    }
}
