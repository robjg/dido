package org.oddjob.dido.poi.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.OurDirs;
import org.oddjob.dido.DataException;

public class PoiAssumtionsTest extends TestCase {

	File workDir;
	
	@Override
	protected void setUp() throws Exception {
	
		super.setUp();
		
		workDir = new OurDirs().relative("work");
	}
	
	
	public void testIndexAssumptions() throws FileNotFoundException, IOException {
		
		Workbook workbook = new XSSFWorkbook();
		
		assertEquals(0, workbook.getNumberOfSheets());
		
		Sheet sheet = workbook.createSheet("Test");
		
		assertEquals(0, sheet.getLastRowNum());
		
		Row row0 = sheet.getRow(0);
		
		assertNull(row0);
		
		sheet.createRow(0);
		
		assertEquals(0, sheet.getLastRowNum());
		
		Row row4 = sheet.createRow(4);
		
		assertEquals(4, sheet.getLastRowNum());
		
		assertEquals(null, sheet.getRow(5));
		assertEquals(null, sheet.getRow(3));
		
		assertEquals(-1, row4.getLastCellNum());
		
		assertEquals(null, row4.getCell(0));
		
		row4.createCell(0);
		
		// Not that last row and last cell are inconsistent!
		assertEquals(1, row4.getLastCellNum());
		
		Cell cell7 = row4.createCell(7);
		
		assertEquals(8, row4.getLastCellNum());
		
		assertEquals(null, row4.getCell(5));
		
		cell7.setCellValue("TEST");
		
		assertEquals("TEST", cell7.getStringCellValue());
		
		// Looks like cell is re-created.
		Cell cell7a = row4.createCell(7);

		assertNotSame(cell7, cell7a);
		
		assertEquals("", cell7a.getStringCellValue());
		
		cell7a.setCellValue("Apples");
		
		workbook.write(new FileOutputStream(
				new File(workDir, "PoiAssumptions.xlsx")));
		
	}
	
	public void testMakingACellBlank() throws DataException {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Test");
		Row row = sheet.createRow(0);
				
		Cell cell1 = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell1.setCellValue("apples");
				
		cell1.setCellType(Cell.CELL_TYPE_BLANK);
		
		assertEquals("", cell1.getRichStringCellValue().toString());
		assertEquals(0.0, cell1.getNumericCellValue());
	}

}
