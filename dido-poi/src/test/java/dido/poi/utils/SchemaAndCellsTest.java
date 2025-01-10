package dido.poi.utils;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.poi.CellInProvider;
import dido.poi.CellProvider;
import dido.poi.RowIn;
import dido.poi.RowsIn;
import dido.poi.data.DataCell;
import dido.poi.data.PoiRowsIn;
import dido.poi.layouts.*;
import dido.poi.style.DefaultStyleProivderFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.DateHelper;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemaAndCellsTest {

    @Test
    void testWithSingleCell() {

        TextCell cell = new TextCell();

        SchemaAndCells<CellProvider> test = SchemaAndCells.fromCells(Collections.singletonList(cell));

        DataSchema schema = test.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(1));
        assertThat(schema.nextIndex(1), is(0));
        assertThat(schema.getFieldNameAt(1), is("f_1"));
        assertThat(schema.getTypeAt(1), is(String.class));
    }


    @Test
    void testFromCellsWithDisparateIndexes() {

        TextCell fruitCell = new TextCell();
        fruitCell.setIndex(3);
        fruitCell.setName("Fruit");

        NumericCell qtyCell = new NumericCell();
        qtyCell.setIndex(7);
        qtyCell.setName("Qty");

        SchemaAndCells<CellProvider> test = SchemaAndCells.fromCells(Arrays.asList(fruitCell, qtyCell));

        DataSchema expectedSchema = DataSchema.builder()
                .addNamedAt(3, "Fruit", String.class)
                .addNamedAt(7, "Qty", Double.class)
                .build();

        assertThat(test.getSchema(), is(expectedSchema));
    }

    @Test
    void testFromSchema() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("String", String.class)
                .addNamed("Boolean", boolean.class)
                .addNamed("Byte", byte.class)
                .addNamed("Short", short.class)
                .addNamed("Int", int.class)
                .addNamed("Long", long.class)
                .addNamed("Float", float.class)
                .addNamed("Double", double.class)
                .addNamed("Date", LocalDateTime.class)
                .build();

        SchemaAndCells<DataCell> test = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromSchema(schema);

        List<DataCell> cells = (List<DataCell>) test.getDataCells();

        DataCell next;

        next = cells.get(0);
        assertThat(next, instanceOf(TextCell.class));
        assertThat(next.getIndex(), is(1));
        assertThat(next.getName(), is("String"));
        assertThat(next.getType(), is(String.class));

        next = cells.get(1);
        assertThat(next, instanceOf(BooleanCell.class));
        assertThat(next.getIndex(), is(2));
        assertThat(next.getName(), is("Boolean"));
        assertThat(next.getType(), is(Boolean.class));

        next = cells.get(2);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(3));
        assertThat(next.getName(), is("Byte"));
        assertThat(next.getType(), is(Byte.class));

        next = cells.get(3);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(4));
        assertThat(next.getName(), is("Short"));
        assertThat(next.getType(), is(Short.class));

        next = cells.get(4);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(5));
        assertThat(next.getName(), is("Int"));
        assertThat(next.getType(), is(Integer.class));

        next = cells.get(5);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(6));
        assertThat(next.getName(), is("Long"));
        assertThat(next.getType(), is(Long.class));

        next = cells.get(6);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(7));
        assertThat(next.getName(), is("Float"));
        assertThat(next.getType(), is(Float.class));

        next = cells.get(7);
        assertThat(next, instanceOf(NumericCell.class));
        assertThat(next.getIndex(), is(8));
        assertThat(next.getName(), is("Double"));
        assertThat(next.getType(), is(Double.class));

        next = cells.get(8);
        assertThat(next, instanceOf(DateCell.class));
        assertThat(next.getIndex(), is(9));
        assertThat(next.getName(), is("Date"));
        assertThat(next.getType(), is(LocalDateTime.class));
    }

    @Test
    void testFromSchemaWithDisparateIndexes() {

        DataSchema schema = DataSchema.builder()
                .addNamedAt(3, "Fruit", String.class)
                .addNamedAt(7, "Qty", Double.class)
                .build();

        SchemaAndCells<DataCell> test = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromSchema(schema);

        List<? extends DataCell> cells = new ArrayList<>(test.getDataCells());

        DataCell fruitCell = cells.get(0);
        assertThat(fruitCell, instanceOf(TextCell.class));
        assertThat(fruitCell.getIndex(), is(3));
        assertThat(fruitCell.getName(), is("Fruit"));
        assertThat(fruitCell.getType(), is(String.class));

        DataCell qtyCell = cells.get(1);
        assertThat(qtyCell, instanceOf(NumericCell.class));
        assertThat(qtyCell.getIndex(), is(7));
        assertThat(qtyCell.getName(), is("Qty"));
        assertThat(qtyCell.getType(), is(Double.class));
    }

    @Test
    void testNullWhenNoSchemaOrCells() {

        assertThat(SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(null, null), nullValue());
        assertThat(SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(null, new String[0]), nullValue());
    }

    @Test
    void fromRowNoHeadings() throws ParseException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row data = sheet.createRow(0);
        data.createCell(0).setCellValue("Apples");
        data.createCell(1).setCellValue(42.0);
        data.createCell(2).setCellValue(124.0);
        Cell dateCell = data.createCell(3);
        dateCell.setCellValue(DateHelper.parseDate("2021-10-01"));
        dateCell.setCellStyle(new DefaultStyleProivderFactory().providerFor(workbook)
                .styleFor(DefaultStyleProivderFactory.DATE_STYLE));

        RowsIn rowsIn = new PoiRowsIn(sheet, 0, 0);
        RowIn rowIn = rowsIn.peekRow();

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(rowIn, null);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("f_1", String.class)
                .addNamed("f_2", Double.class)
                .addNamed("f_3", Double.class)
                .addNamed("f_4", LocalDateTime.class)
                .build();

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void fromRowNoHeadingsPartialSchema() throws ParseException {

        DataSchema partialSchema = DataSchema.builder()
                .addNamedAt(2, "Qty", int.class)
                .addNamedAt(27, "Price", int.class) // won't be matched
                .build();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row data = sheet.createRow(0);
        data.createCell(0).setCellValue("Apples");
        data.createCell(1).setCellValue(42.0);
        data.createCell(2).setCellValue(124.0);
        Cell dateCell = data.createCell(3);
        dateCell.setCellValue(DateHelper.parseDate("2021-10-01"));
        dateCell.setCellStyle(new DefaultStyleProivderFactory().providerFor(workbook)
                .styleFor(DefaultStyleProivderFactory.DATE_STYLE));

        RowsIn rowsIn = new PoiRowsIn(sheet, 0, 0);
        RowIn rowIn = rowsIn.peekRow();

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(rowIn, null, partialSchema);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("f_1", String.class)
                .addNamed("Qty", int.class)
                .addNamed("f_3", Double.class)
                .addNamed("f_4", LocalDateTime.class)
                .build();

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void fromRowAndHeadingsWithHeadings() throws ParseException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("Fruit");
        headRow.createCell(1).setCellValue("Qty");
        headRow.createCell(2).setCellValue("Price");
        headRow.createCell(3).setCellValue("BestBefore");

        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue("Apples");
        data.createCell(1).setCellValue(42.0);
        data.createCell(2).setCellValue(124.0);
        Cell dateCell = data.createCell(3);
        dateCell.setCellValue(DateHelper.parseDate("2021-10-01"));
        dateCell.setCellStyle(new DefaultStyleProivderFactory().providerFor(workbook)
                .styleFor(DefaultStyleProivderFactory.DATE_STYLE));

        RowsIn rowsIn = new PoiRowsIn(sheet, 0, 0);
        String[] headings = rowsIn.headerRow();
        RowIn rowIn = rowsIn.peekRow();

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(rowIn, headings);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", Double.class)
                .addNamed("Price", Double.class)
                .addNamed("BestBefore", LocalDateTime.class)
                .build();

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void fromRowAndHeadingsWithHeadingsPartialSchema() throws ParseException {

        DataSchema partialSchema = DataSchema.builder()
                .addNamedAt(1, "Qty", int.class)
                .addNamedAt(3, "ThePrice", double.class) // won't be matched
                .addNamedAt(22, "BestBefore", LocalDate.class)
                .build();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("Fruit");
        headRow.createCell(1).setCellValue("Qty");
        headRow.createCell(2).setCellValue("Price");
        headRow.createCell(3).setCellValue("BestBefore");

        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue("Apples");
        data.createCell(1).setCellValue(42.0);
        data.createCell(2).setCellValue(124.0);
        Cell dateCell = data.createCell(3);
        dateCell.setCellValue(DateHelper.parseDate("2021-10-01"));
        dateCell.setCellStyle(new DefaultStyleProivderFactory().providerFor(workbook)
                .styleFor(DefaultStyleProivderFactory.DATE_STYLE));

        RowsIn rowsIn = new PoiRowsIn(sheet, 0, 0);
        String[] headings = rowsIn.headerRow();
        RowIn rowIn = rowsIn.peekRow();

        SchemaAndCells<DataCell> schemaAndCells = SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(rowIn, headings, partialSchema);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", Double.class)
                .addNamed("BestBefore", LocalDate.class)
                .build();

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema, is(expectedSchema));
    }

    @Test
    void whenCellsAndSchemaThenBothPastAsIs() {

        // Cells and Schema are not related.

        DataSchema schema = DataSchema.builder()
                .addNamedAt(5, "Name", String.class)
                .addNamedAt(20, "Address", String.class)
                .build();

        CellInProvider cell1 = mock(CellInProvider.class);
        when(cell1.getName()).thenReturn("Fruit");

        CellInProvider cell2 = mock(CellInProvider.class);
        when(cell2.getName()).thenReturn("Qty");

        SchemaAndCells<CellInProvider> test = SchemaAndCells
                .withCells(List.of(cell1, cell2))
                .fromSchema(schema);

        assertThat(test.getDataCells(), contains(cell1, cell2));
        assertThat(test.getSchema(), is(schema));
    }

}