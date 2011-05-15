package org.oddjob.poi;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class SimpleHeadingsTest extends TestCase {

	public void testHeadingColumns() {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();

		Row row = sheet.createRow(0);
		
		row.createCell(0).setCellValue("Name");
		row.createCell(1).setCellValue("Age");
		row.createCell(2).setCellValue("City");
		
		SimpleHeadings test = new SimpleHeadings(row, 0);
		
		assertEquals(2, test.position("City"));
		assertEquals(1, test.position("Age"));
		assertEquals(0, test.position("Name"));
		assertEquals(-1, test.position("Fruit"));
	}
	
	public void testMissingColumns() {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();

		Row row = sheet.createRow(0);
		
		row.createCell(3).setCellValue("Name");
		row.createCell(4).setCellValue("Age");
		row.createCell(5).setCellValue("City");
		
		SimpleHeadings test = new SimpleHeadings(row, 0);
		
		assertEquals(-1, test.position("City"));
		assertEquals(-1, test.position("Age"));
		assertEquals(-1, test.position("Name"));
		assertEquals(-1, test.position("Fruit"));
	}
}
