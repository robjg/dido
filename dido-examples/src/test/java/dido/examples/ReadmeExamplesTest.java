package dido.examples;

import dido.csv.DataInCsv;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.json.DataOutJson;
import dido.json.JsonDidoFormat;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.oddjob.tools.ConsoleCapture;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ReadmeExamplesTest {


    @Test
    void firstExamples() throws IOException, JSONException {

        try (InputStream in = Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitNoHeader.csv"));
             OutputStream out = Files.newOutputStream(Path.of("Fruit.csv"))) {

            in.transferTo(out);

        }

        // #snippet1{
        List<DidoData> didoData;

        try (DataIn in = DataInCsv.fromPath(Path.of("Fruit.csv"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple","5","19.50"),
                DidoData.of("Orange","2","35.24"),
                DidoData.of("Pear","3","26.84")));
        // }#snippet1

        ConsoleCapture consoleCapture1 = new ConsoleCapture();
        try (ConsoleCapture.Close ignore = consoleCapture1.captureConsole()) {

            captureSnippet2(didoData);
        }
        String result1 = consoleCapture1.getAll();

        String expected1 = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitAllText.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected1, result1, JSONCompareMode.LENIENT);

        System.out.println();

        // #snippet3{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();
        // }#snippet3

        ConsoleCapture consoleCapture2 = new ConsoleCapture();
        try (ConsoleCapture.Close ignore = consoleCapture2.captureConsole()) {

            captureSnippet4(schema);
        }
        String result2 = consoleCapture2.getAll();

        String expected2 = new String(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/FruitLines.json")).readAllBytes(),
                StandardCharsets.UTF_8);

        JSONAssert.assertEquals(expected2, result2, JSONCompareMode.LENIENT);

        Files.delete(Path.of("Fruit.csv"));
    }

    void captureSnippet2(List<DidoData> didoData) {

        // #snippet2{
        try (DataOut out = DataOutJson.toOutputStream(System.out)) {

            didoData.forEach(out);
        }
        // }#snippet2
    }

    void captureSnippet4(DataSchema schema) {

        // #snippet4{
        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .fromPath(Path.of("Fruit.csv"));
             DataOut out = DataOutJson.with()
                     .outFormat(JsonDidoFormat.LINES)
                     .toOutputStream(System.out)) {

            in.stream().forEach(out);
        }
        // }#snippet4
    }

}
