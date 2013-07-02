package org.oddjob.poi;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.dido.DataException;

public class PoiSheetsTest extends TestCase {

	public void testgetNextWhenExisting() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		PoiSheetOut test1 = new PoiSheetOut(sheet);
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(0, Cell.CELL_TYPE_STRING);
		assertEquals(Cell.CELL_TYPE_STRING, cell1.getCellType());
		assertNull(cell1.getRichStringCellValue());
		
		cell1.setCellValue("apples");
				
		PoiSheetIn test2 = new PoiSheetIn(sheet);
		assertTrue(test2.nextRow());
		
		TupleIn tupleIn = test2.provideDataIn(TupleIn.class);
		
		Cell cell2 = tupleIn.getCell(0);
		
		assertEquals(Cell.CELL_TYPE_STRING, cell2.getCellType());
		assertNotNull(cell2.getRichStringCellValue());
		assertEquals("apples", cell2.getRichStringCellValue().toString());
		
		assertNull(tupleIn.getCell(1));
		assertFalse(test2.nextRow());
	}
	
	public void testDifferentCellTypes() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		PoiSheetOut test1 = new PoiSheetOut(sheet);
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(0, Cell.CELL_TYPE_BLANK);
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
		
	}
	
	public void testDateCellTypes() throws ParseException, DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		PoiSheetOut test1 = new PoiSheetOut(sheet);
		test1.nextRow();
		
		Date theDate = DateHelper.parseDateTime("2010-12-25 12:45");
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(0, Cell.CELL_TYPE_BLANK);
		cell1.setCellValue(theDate);
		
		assertEquals(Cell.CELL_TYPE_NUMERIC, cell1.getCellType());

		assertEquals(theDate, cell1.getDateCellValue());
		
		CreationHelper createHelper = workbook.getCreationHelper();
		  
		CellStyle cellStyle = workbook.createCellStyle();
		    
		cellStyle.setDataFormat(
		        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		
		cell1.setCellStyle(cellStyle);

		try {
			cell1.getStringCellValue();
			fail("Shouldn't be possible.");
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	public void testMakingACellBlank() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		PoiSheetOut test1 = new PoiSheetOut(sheet);
		test1.nextRow();
		
		TupleOut tupleOut = test1.provideDataOut(TupleOut.class);
		
		Cell cell1 = tupleOut.createCell(0, Cell.CELL_TYPE_STRING);
		cell1.setCellValue("apples");
				
		cell1.setCellType(Cell.CELL_TYPE_BLANK);
		
		assertEquals("", cell1.getRichStringCellValue().toString());
		assertEquals(0.0, cell1.getNumericCellValue());
	}

	public void testHeadings() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		PoiSheetOut testOut = new PoiSheetOut(sheet, 
				new DefaultStyleFactory().providerFor(workbook));
		
		testOut.headerRow(null);
		
		TupleOut tupleOut = testOut.provideDataOut(TupleOut.class);
		
		assertEquals(0, tupleOut.indexForHeading("Name"));
		assertEquals(1, tupleOut.indexForHeading("Age"));

		testOut.nextRow();
		
		tupleOut.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("John");
		tupleOut.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(25);
		
		PoiSheetIn testIn = new PoiSheetIn(sheet);
		testIn.headerRow();
		
		TupleIn tupleIn = testIn.provideDataIn(TupleIn.class);
		
		assertEquals(0, tupleIn.indexForHeading("Name"));
		assertEquals(1, tupleIn.indexForHeading("Age"));

		assertTrue(testIn.nextRow());
		assertEquals(25.0, tupleIn.getCell(1).getNumericCellValue(), 0.01);
		assertEquals("John", tupleIn.getCell(0).getStringCellValue());
	}
	
}                                                       
