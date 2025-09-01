package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class CsvDelimitersExampleTest {

    @Test
    void example() {

        // #customCsvOut{
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = DidoData.withSchema(schema)
                .many()
                .of("Apple", "5", "19.50")
                .of("Orange", "2", "35.24")
                .of("Pear", "3", "26.84")
                .toList();

        Writer writer = new StringWriter();

        try (DataOut out = DataOutCsv.with()
                .csvFormat(CSVFormat.DEFAULT.builder()
                        .setDelimiter('|')
                        .setRecordSeparator('\n')
                        .build())
                .toWriter(writer)) {

            didoData.forEach(out);
        }

        assertThat(writer.toString(),
                is("""
                        Apple|5|19.50
                        Orange|2|35.24
                        Pear|3|26.84
                        """));
        // }#customCsvOut

        // #customCsvIn{
        Reader reader = new StringReader(writer.toString());

        List<DidoData> dataBack;

        try (DataIn in = DataInCsv.with()
                .csvFormat(CSVFormat.DEFAULT.builder()
                        .setDelimiter('|')
                        .setRecordSeparator('\n')
                        .build())
                .fromReader(reader)) {

            dataBack = in.stream().toList();
        }

        assertThat(dataBack, contains(
                DidoData.of("Apple", "5", "19.50"),
                DidoData.of("Orange", "2", "35.24"),
                DidoData.of("Pear", "3", "26.84")));
        // }#customCsvIn
    }
}
