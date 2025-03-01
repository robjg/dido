package dido.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;

public class CsvParserAssumptionsTest {

    @Test
    void heading() throws IOException {

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .build();

        CSVParser parser = csvFormat.parse(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getResourceAsStream("/data/FruitWithHeader.csv"))));

        List<CSVRecord> records = parser.getRecords();

        CSVRecord row1 = records.get(0);

        assertThat(row1.toList(), Matchers.contains("Fruit","Qty","Price"));
    }
}
