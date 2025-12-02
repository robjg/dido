package dido.json;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.tools.ConsoleCapture;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FromJsonStringTypeTest {

    @Test
    void example1() throws IOException, JSONException {

        File file = new File(Objects.requireNonNull(getClass().getResource(
                "FromJsonMapExample.xml")).getFile());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(file);

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {

            oddjob.run();
        }

        assertTrue(oddjob.lastStateEvent().getState().isComplete());

        String actual = console.getAll();

        String expected = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("FromJsonMapExampleOut.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);

        oddjob.destroy();
    }
}