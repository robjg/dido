package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.lines.DataInLines;
import dido.text.DataOutTextTable;
import org.junit.jupiter.api.Test;
import org.oddjob.tools.ConsoleCapture;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TextExamplesTest {

    @Test
    void writeExample() {

        // #snippet1{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = DidoData.withSchema(schema)
                .many()
                .of("Apple", 5, 19.50)
                .of("Orange", 2, 35.24)
                .of("Pear", 3, 26.84)
                .toList();
        // }#snippet1

        ConsoleCapture consoleCapture1 = new ConsoleCapture();
        try (ConsoleCapture.Close ignore = consoleCapture1.captureConsole()) {

            captureSnippet2(didoData);
        }

        String[] result1 = consoleCapture1.getLines();

        try (DataIn in = DataInLines.fromInputStream(Objects.requireNonNull(
                getClass().getResourceAsStream("/examples/Fruit.txt")))) {
            String[] expected1 = in.stream()
                    .map(d -> d.getStringAt(1))
                    .toArray(String[]::new);

            assertThat(result1, is(expected1));
        }
    }

    void captureSnippet2(List<DidoData> didoData) {

        // #snippet2{
        try (DataOut out = DataOutTextTable.toOutputStream(System.out)) {

            didoData.forEach(out);
        }
        // }#snippet2
    }

}
