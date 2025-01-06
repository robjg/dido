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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.DateHelper;

import java.text.ParseException;
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
        assertThat(next instanceof TextCell, is(true));

        next = cells.get(1);
        assertThat(next instanceof BooleanCell, is(true));

        next = cells.get(2);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(3);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(4);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(5);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(6);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(7);
        assertThat(next instanceof NumericCell, is(true));

        next = cells.get(8);
        assertThat(next instanceof DateCell, is(true));
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
        assertThat(fruitCell, Matchers.instanceOf(TextCell.class));
        assertThat(fruitCell.getIndex(), is(3));
        assertThat(fruitCell.getName(), is("Fruit"));

        DataCell qtyCell = cells.get(1);
        assertThat(qtyCell, instanceOf(NumericCell.class));
        assertThat(qtyCell.getIndex(), is(7));
        assertThat(qtyCell.getName(), is("Qty"));
    }

    @Test
    void testNullWhenNoSchemaOrCells() {

        assertThat(SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(null, null), nullValue());
        assertThat(SchemaAndCells.withCellFactory(new DataCellFactory())
                .fromRowAndHeadings(null, new String[0]), nullValue());
    }

    @Test
    void testFromRowAndHeadingsWithHeadings() throws ParseException {

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

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema.getTypeAt(2), is(Double.class));
        assertThat(schema.getTypeNamed("Qty"), is(Double.class));
        assertThat(schema.getTypeAt(3), is(Double.class));
        assertThat(schema.getTypeNamed("Price"), is(Double.class));
        assertThat(schema.getTypeAt(4), is(LocalDateTime.class));
        assertThat(schema.getTypeNamed("BestBefore"), is(LocalDateTime.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(0));
        assertThat(schema.getFieldNames(), Matchers.contains("Fruit", "Qty", "Price", "BestBefore"));
    }

    @Test
    void withCellsAndSchemaByName() {

        DataSchema schema = DataSchema.builder()
                .addNamedAt(20, "Fruit", String.class)
                .build();

        CellInProvider cell1 = mock(CellInProvider.class);
        when(cell1.getName()).thenReturn("Fruit");

        SchemaAndCells<CellInProvider> test = SchemaAndCells.withCells(List.of(cell1))
                .fromSchema(schema);

        assertThat(test.getDataCells(), contains(cell1));
        assertThat(test.getSchema(), is(schema));
    }

}