package org.oddjob.poi;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataCellTest extends TestCase {

	
	public void testReference() {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		assertEquals(0, sheet.getLastRowNum());
		sheet.createRow(10);
		assertEquals(10, sheet.getLastRowNum());
		
		DataCell<String> test = new TextCell();
		
		SheetIn in = new PoiSheetIn(sheet);
		assertTrue(in.nextRow());
		
		test.begin(in);
		
		assertEquals("$A$1", test.getReference());
	}
	
}
