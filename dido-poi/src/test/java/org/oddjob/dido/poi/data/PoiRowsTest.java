package org.oddjob.dido.poi.data;

import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Cell;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;
import org.oddjob.dido.poi.*;
import org.oddjob.dido.poi.layouts.*;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleFactoryRegistry;

import java.text.ParseException;
import java.util.Date;

public class PoiRowsTest extends TestCase {

	public void testWriteNextAndGetNext() throws DataException {
		
		TextCell text = new TextCell();
		text.setColumnIndex(1);

		PoiWorkbook workbook = new PoiWorkbook();

		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);

		CellOut<Object> cellOut1 = (CellOut<Object>) tupleOut.outFor(text);
				
		cellOut1.setData("apples");

		Cell cell1 = sheetOut.getTheSheet().getRow(0).getCell(0);
		assertEquals(Cell.CELL_TYPE_STRING, cell1.getCellType());
				
		test1.nextRow();
		
		cellOut1.setData("oranges");
				
		assertEquals(2, test1.getLastRow());
		
		bookOut.close();
		
		// Read Test
		////////////
		
		BookIn bookIn = workbook.provideDataIn(BookIn.class);		
		SheetIn sheetIn = bookIn.provideDataIn(SheetIn.class);
		
		RowsIn test2 = new PoiRowsIn(sheetIn, 0, 0);
		
		assertEquals(true, test2.nextRow());
		
		TupleIn tupleIn = test2.provideDataIn(TupleIn.class);
		
		CellIn<String> cellIn1 = (CellIn<String>) tupleIn.inFor(text);
		
		String data = cellIn1.getData();
		
		assertEquals("apples", data);
		
		assertTrue(test2.nextRow());
		
		data = cellIn1.getData();
		
		assertEquals("oranges", data);
		
		assertFalse(test2.nextRow());
	}
	
	public void testDifferentCellTypes() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		BlankCell blank = new BlankCell();
		blank.setColumnIndex(1);

		CellOut<Object> cellOut1 = (CellOut<Object>) tupleOut.outFor(blank);
		cellOut1.setData(null);
		
		Cell cell1 = sheetOut.getTheSheet().getRow(0).getCell(0);
		assertEquals(Cell.CELL_TYPE_BLANK, cell1.getCellType());
		
		TextCell text = new TextCell();
		text.setColumnIndex(1);

		cellOut1 = (CellOut<Object>) tupleOut.outFor(text);
		cellOut1.setData("apples");
		
		cell1.setCellValue("apples");
		
		assertEquals(Cell.CELL_TYPE_STRING, cell1.getCellType());

		try {
			cell1.getCellFormula();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
		
		try {
			cell1.getNumericCellValue();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
		
		NumericCell numeric = new NumericCell();
		numeric.setColumnIndex(1);

		cellOut1 = (CellOut<Object>) tupleOut.outFor(numeric);
		cellOut1.setData(12.2);
		
		assertEquals(Cell.CELL_TYPE_NUMERIC, cell1.getCellType());
		
		NumericFormulaCell formula = new NumericFormulaCell();
		formula.setColumnIndex(1);
		formula.setFormula("6/2");
		
		cellOut1 = (CellOut<Object>) tupleOut.outFor(formula);
		cellOut1.setData(null);
		
		assertEquals(Cell.CELL_TYPE_FORMULA, cell1.getCellType());
		
		bookOut.close();
	}
	
	public void testHeadings() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		TextCell cell1 = new TextCell();
		cell1.setLabel("Name");
		NumericCell cell2 = new NumericCell();
		cell1.setLabel("Age");
		
		RowsOut testOut = new PoiRowsOut(sheetOut, 8, 4);
		
		testOut.headerRow(null);
		
		TupleOut tupleOut = testOut.provideDataOut(TupleOut.class);
		
		CellOut<String> cellOut1 = (CellOut<String>) tupleOut.outFor(cell1);
		CellOut<Double> cellOut2 = (CellOut<Double>) tupleOut.outFor(cell2);
		
		testOut.nextRow();
		
		cellOut1.setData("John");
		cellOut2.setData(25.0);
		
		bookOut.close();
		
		///
		// Read Part
	
		BookIn bookIn = workbook.provideDataIn(BookIn.class);
		SheetIn sheetIn = bookIn.provideDataIn(SheetIn.class);
		
		RowsIn testIn = new PoiRowsIn(sheetIn, 8, 4);
				
		testIn.headerRow();
		
		TupleIn tupleIn = testIn.provideDataIn(TupleIn.class);
		
		CellIn<String> cellIn1 = (CellIn<String>) tupleIn.inFor(cell1);
		CellIn<Double> cellIn2 = (CellIn<Double>) tupleIn.inFor(cell2);
		
		assertEquals(1, cellIn1.getColumnIndex());
		assertEquals(2, cellIn2.getColumnIndex());

		assertTrue(testIn.nextRow());
		
		assertEquals("John", cellIn1.getData());
		assertEquals(25.0, cellIn2.getData(), 0.01);
		
		assertFalse(testIn.nextRow());
	}
	
	/**
	 * An old test refactored for changes so much that I'm not quite sure 
	 * what it proves any more.
	 * 
	 * @throws ParseException
	 * @throws DataException
	 */
	public void testDateCellTypes() throws ParseException, DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		StyleBean styleBean = new StyleBean();
		styleBean.setFormat("m/d/yy h:mm");
		
		StyleFactoryRegistry styles = new StyleFactoryRegistry();
		styles.registerStyle("my-date-format", styleBean);
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		bookOut.addStyleFactory(styles);
		
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		test1.nextRow();
		
		Date theDate = DateHelper.parseDateTime("2010-12-25 12:45");
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		BlankCell blank = new BlankCell();
		blank.setColumnIndex(1);

		CellOut<Object> cellOut1 = (CellOut<Object>) tupleOut.outFor(blank);
		cellOut1.setData(null);
		
		Cell cell1 = sheetOut.getTheSheet().getRow(0).getCell(0);
		
		assertEquals(Cell.CELL_TYPE_BLANK, cell1.getCellType());
		
		DateCell dateColumn = new DateCell();
		dateColumn.setColumnIndex(1);
		
		cellOut1 = (CellOut<Object>) tupleOut.outFor(dateColumn);
		cellOut1.setData(theDate);
		
		assertEquals(Cell.CELL_TYPE_NUMERIC, cell1.getCellType());

		assertEquals(theDate, cell1.getDateCellValue());
				
		cell1.setCellStyle(tupleOut.styleFor("my-date-format"));

		try {
			cell1.getStringCellValue();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}	
}
