package dido.poi.utils;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.poi.RowIn;
import dido.poi.RowsIn;
import dido.poi.data.DataCell;
import dido.poi.data.PoiRowsIn;
import dido.poi.layouts.BooleanCell;
import dido.poi.layouts.DateCell;
import dido.poi.layouts.NumericCell;
import dido.poi.layouts.TextCell;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SchemaAndCellsTest {

    @Test
    void testWithSingleCell() {

        TextCell cell =  new TextCell();

        SchemaAndCells test = SchemaAndCells.fromSchemaOrCells(null, Collections.singletonList(cell));

        DataSchema schema = test.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(1));
        assertThat(schema.nextIndex(1), is(0));
        assertThat(schema.getFieldNameAt(1), is("[1]"));
        assertThat(schema.getTypeAt(1), is(String.class));
    }


    @Test
    void testSchemaWithDisparateIndexes() {

        TextCell fruitCell = new TextCell();
        fruitCell.setIndex(3);
        fruitCell.setName("Fruit");

        NumericCell<Double> qtyCell = new NumericCell<>();
        qtyCell.setIndex(7);
        qtyCell.setName("Qty");

        SchemaAndCells test = SchemaAndCells.fromSchemaOrCells(null, Arrays.asList(fruitCell, qtyCell));

        DataSchema schema = test.getSchema();

        assertThat(schema.firstIndex(), is(3));
        assertThat(schema.lastIndex(), is(7));
        assertThat(schema.nextIndex(3), is(7));
        assertThat(schema.getFieldNameAt(3), is("Fruit"));
        assertThat(schema.getFieldNameAt(7), is("Qty"));
        assertThat(schema.getTypeAt(3), is(String.class));
        assertThat(schema.getTypeAt(7), is(Double.class));
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
                .addNamed("Date", Date.class)
                .build();

        SchemaAndCells test = SchemaAndCells.fromSchemaOrCells(schema, null);

        //noinspection unchecked
        List<DataCell<?>> cells = (List<DataCell<?>>) test.getDataCells();

        DataCell<?> next;

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
    void testNullWhenNoSchemaOrCells() {

        assertThat(SchemaAndCells.fromSchemaOrCells(null, null), nullValue());
        assertThat(SchemaAndCells.fromSchemaOrCells(null, Collections.emptyList()), nullValue());
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

        SchemaAndCells schemaAndCells = SchemaAndCells.fromRowAndHeadings(rowIn, headings);

        DataSchema schema = schemaAndCells.getSchema();

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(4));
        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema.getTypeAt(2), is(Double.class));
        assertThat(schema.getTypeNamed("Qty"), is(Double.class));
        assertThat(schema.getTypeAt(3), is(Double.class));
        assertThat(schema.getTypeNamed("Price"), is(Double.class));
        assertThat(schema.getTypeAt(4), is(Date.class));
        assertThat(schema.getTypeNamed("BestBefore"), is(Date.class));
        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.nextIndex(3), is(4));
        assertThat(schema.nextIndex(4), is(0));
        assertThat(schema.getFieldNames(), Matchers.contains("Fruit", "Qty", "Price", "BestBefore"));
    }
}