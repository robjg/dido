package org.oddjob.dido.poi.utils;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.arooa.utils.DateHelper;

public class CellHelperTest extends TestCase {

	public void testWriteAndRead() throws ParseException {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		Row row = sheet.createRow(2);
		
		Cell cell = row.createCell(3);

		CellHelper test = new CellHelper();
		
		// Numeric
		
		test.setCellValue(cell, 42.2);
		
		Double numericResult = test.getCellValue(cell, Double.class);
		
		assertEquals(42.2, numericResult);
		
		// Date
		
		test.setCellValue(cell, DateHelper.parseDate("2013-08-30"));
		
		Date dateResult = test.getCellValue(cell, Date.class);
		
		assertEquals(DateHelper.parseDate("2013-08-30"), dateResult);
		
		// Boolean
		
		test.setCellValue(cell, true);
		
		Boolean booleanResult = test.getCellValue(cell, Boolean.class);
		
		assertEquals(true, (boolean) booleanResult);
		
		// Text
		
		test.setCellValue(cell, "apple");
		
		String stringResult = test.getCellValue(cell, String.class);
		
		assertEquals("apple", stringResult);
		
		// Null
		
		test.setCellValue(cell, null);
		
		Object nullResult = test.getCellValue(cell, Object.class);
		
		assertEquals(null, nullResult);
	}
}
