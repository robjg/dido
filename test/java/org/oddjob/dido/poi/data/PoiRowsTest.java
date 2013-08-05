package org.oddjob.dido.poi.data;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.RowsIn;
import org.oddjob.dido.poi.RowsOut;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.TupleIn;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.poi.style.StyleProviderFactory;

public class PoiRowsTest extends TestCase {

	public void testWriteNextAndGetNext() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();

		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(1, Cell.CELL_TYPE_STRING);
		assertEquals(Cell.CELL_TYPE_STRING, cell1.getCellType());
		
		// Not that this is null with version Excel 97.
		assertEquals("", cell1.getRichStringCellValue().toString());
		
		cell1.setCellValue("apples");

		test1.nextRow();
		
		cell1 = tupleOut.createCell(1, Cell.CELL_TYPE_STRING);
		cell1.setCellValue("oranges");
		
		assertEquals(2, test1.getLastRow());
		
		bookOut.close();
		
		// Read Test
		////////////
		
		BookIn bookIn = workbook.provideDataIn(BookIn.class);		
		SheetIn sheetIn = bookIn.provideDataIn(SheetIn.class);
		
		RowsIn test2 = new PoiRowsIn(sheetIn, 0, 0);
		
		assertEquals(true, test2.nextRow());
		
		TupleIn tupleIn = test2.provideDataIn(TupleIn.class);
		
		Cell cell2 = tupleIn.getCell(1);
		
		assertEquals(Cell.CELL_TYPE_STRING, cell2.getCellType());
		assertNotNull(cell2.getRichStringCellValue());
		assertEquals("apples", cell2.getRichStringCellValue().toString());
		
		assertTrue(test2.nextRow());
		
		cell2 = tupleIn.getCell(1);
		assertEquals("oranges", cell2.getStringCellValue().toString());
		
		assertFalse(test2.nextRow());
	}
	
	public void testDifferentCellTypes() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(1, Cell.CELL_TYPE_BLANK);
		assertEquals(Cell.CELL_TYPE_BLANK, cell1.getCellType());
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
		
		cell1.setCellValue(12.2);
		assertEquals(Cell.CELL_TYPE_NUMERIC, cell1.getCellType());
		
		cell1.setCellFormula("6/2");
		assertEquals(Cell.CELL_TYPE_FORMULA, cell1.getCellType());
		
		bookOut.close();
	}
	
	public void testHeadings() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut testOut = new PoiRowsOut(sheetOut, 8, 4);
		
		testOut.headerRow(null);
		
		TupleOut tupleOut = testOut.provideDataOut(TupleOut.class);
		
		assertEquals(1, tupleOut.indexForHeading("Name"));
		assertEquals(2, tupleOut.indexForHeading("Age"));

		testOut.nextRow();
		
		tupleOut.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("John");
		tupleOut.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(25);
		
		bookOut.close();
		
		///
		// Read Part
	
		BookIn bookIn = workbook.provideDataIn(BookIn.class);
		SheetIn sheetIn = bookIn.provideDataIn(SheetIn.class);
		
		RowsIn testIn = new PoiRowsIn(sheetIn, 8, 4);
				
		testIn.headerRow();
		
		TupleIn tupleIn = testIn.provideDataIn(TupleIn.class);
		
		assertEquals(1, tupleIn.indexForHeading("Name"));
		assertEquals(2, tupleIn.indexForHeading("Age"));

		assertTrue(testIn.nextRow());
		
		assertEquals("John", tupleIn.getCell(1).getStringCellValue());
		assertEquals(25.0, tupleIn.getCell(2).getNumericCellValue(), 0.01);
		
		assertFalse(testIn.nextRow());
	}
	
	public void testDateCellTypes() throws ParseException, DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		StyleBean styleBean = new StyleBean();
		styleBean.setFormat("m/d/yy h:mm");
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		bookOut.addStyleFactory(new StyleProviderFactory() {
			
			@Override
			public StyleProvider providerFor(final Workbook workbook) {
				return new StyleProvider() {
					@Override
					public CellStyle styleFor(String styleName) {
						if (!"my-date-format".equals(styleName)) {
							return null;
						}
						CreationHelper createHelper = workbook.getCreationHelper();
						  
						CellStyle cellStyle = workbook.createCellStyle();
						    
						cellStyle.setDataFormat(
						        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
						
						return cellStyle;
					}
				};
			}
		});
		
		SheetOut sheetOut = bookOut.provideDataOut(SheetOut.class);
		
		RowsOut test1 = new PoiRowsOut(sheetOut, 0, 0);
		test1.nextRow();
		
		Date theDate = DateHelper.parseDateTime("2010-12-25 12:45");
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(1, Cell.CELL_TYPE_BLANK);
		cell1.setCellValue(theDate);
		
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
