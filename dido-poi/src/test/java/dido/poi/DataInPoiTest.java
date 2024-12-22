package dido.poi;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.how.DataIn;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataInPoiTest {

    @Test
    void simpleRead() {

        List<DidoData> expected = List.of(
                ArrayData.of("Apple", 5.0, 23.5, date("2024-11-19")),
                ArrayData.of("Orange", 3.0, 47.2, date("2024-12-05")),
                ArrayData.of("Pear", 8.0, 34.2, date("2025-01-08")));

        try (DataIn in = DataInPoi.fromInputStream(
                getClass().getResourceAsStream("/excel/SimpleTableNoHeadings.xlsx"))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
        }
    }

    @Test
    void readWithSchema() {

        DataSchema schema = MapData.schemaBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .addNamed("SellBy", LocalDateTime.class)
                .build();

        List<DidoData> expected = ArrayData.valuesForSchema(schema)
                .many()
                .of("Apple", 5, 23.5, date("2024-11-19"))
                .of("Orange", 3, 47.2, date("2024-12-05"))
                .of("Pear", 8, 34.2, date("2025-01-08"))
                .toList();

        try (DataIn in = DataInPoi.with()
                .schema(schema)
                .fromInputStream(
                getClass().getResourceAsStream("/excel/SimpleTableNoHeadings.xlsx"))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results, is(expected));
        }
    }

    @Test
    void readPartialSchema() {

        DataSchema schema = MapData.schemaBuilder()
                .addNamedAt(2, "Qty", int.class)
                .build();

        DataSchema expectedSchema = MapData.schemaBuilder()
                .addNamed("f_1", String.class)
                .addNamed("Qty", Integer.class)
                .addNamed("f_3", Double.class)
                .addNamed("f_4", LocalDateTime.class)
                .build();

        List<DidoData> expected = ArrayData.valuesForSchema(expectedSchema)
                .many()
                .of("Apple", 5, 23.5, date("2024-11-19"))
                .of("Orange", 3, 47.2, date("2024-12-05"))
                .of("Pear", 8, 34.2, date("2025-01-08"))
                .toList();

        try (DataIn in = DataInPoi.with()
                .schema(schema)
                .partialSchema(true)
                .fromInputStream(
                        getClass().getResourceAsStream("/excel/SimpleTableNoHeadings.xlsx"))) {

            List<DidoData> results = in.stream().collect(Collectors.toList());

            assertThat(results.get(0).getSchema(), is(expectedSchema));

            assertThat(results, is(expected));
        }
    }

    static LocalDateTime date(String date) {
        return LocalDateTime.parse(date + "T00:00:00");
    }
}