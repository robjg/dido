package dido.poi.layouts;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.ArrayData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.poi.data.PoiWorkbook;
import dido.poi.style.DefaultStyleProivderFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class DateCellTest {

    @Test
    void writeAndReadWithDefaults() {

        PoiWorkbook workbook = new PoiWorkbook();

        DateCell test = new DateCell();

        DataRows rows = new DataRows();
        rows.setOf(0, test);

        try (DataOut writer = rows.outTo(workbook)) {

            writer.accept(ArrayData.of(LocalDateTime.parse("2021-09-22T00:00:00")));
        }

        // Check what we wrote

        Cell dobCell = workbook.getWorkbook().getSheetAt(0).getRow(0).getCell(0);
        assertThat(dobCell.getCellType(), is(CellType.NUMERIC));

        CellStyle cellStyle = dobCell.getCellStyle();
        assertThat(cellStyle.getDataFormatString(), is(DefaultStyleProivderFactory.DATE_FORMAT));

        assertThat(dobCell.toString(), is("2021-09-22"));

        // Read side.

        try (DataIn reader = rows.inFrom(workbook)) {

            List<DidoData> results = reader.stream()
                    .collect(Collectors.toList());

            assertThat(results, contains(ArrayData.of(LocalDateTime.parse("2021-09-22T00:00:00"))));
        }
    }

    @Test
    void writeAndReadWithDateTypeWithStringConversion() {

        PoiWorkbook workbook = new PoiWorkbook();

        DidoConversionProvider conversionProvider = DefaultConversionProvider
                .with()
                .conversion(String.class, LocalDateTime.class,
                        (String s) -> LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                .atStartOfDay())
                .conversion(LocalDateTime.class, String.class,
                        (Function<LocalDateTime, String>) dataTime -> dataTime.toLocalDate().toString())
                .make();

        DataSchema schema = DataSchema.builder()
                .add(String.class)
                .build();

        DateCell test = new DateCell();

        DataRows rows = new DataRows();
        rows.setConversionProvider(conversionProvider);
        rows.setSchema(schema);
        rows.setOf(0, test);

        try (DataOut writer = rows.outTo(workbook)) {

            writer.accept(ArrayData.of("2021-09-22"));
        }

        // Check what we wrote

        Cell dobCell = workbook.getWorkbook().getSheetAt(0).getRow(0).getCell(0);
        assertThat(dobCell.getCellType(), is(CellType.NUMERIC));

        CellStyle cellStyle = dobCell.getCellStyle();
        assertThat(cellStyle.getDataFormatString(), is(DefaultStyleProivderFactory.DATE_FORMAT));

        assertThat(dobCell.toString(), is("2021-09-22"));

        // Read side.

        try (DataIn reader = rows.inFrom(workbook)) {

            List<DidoData> results = reader.stream()
                    .collect(Collectors.toList());

            assertThat(results, contains(ArrayData.of("2021-09-22")));
        }
    }

}
