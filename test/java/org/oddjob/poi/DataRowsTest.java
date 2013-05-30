package org.oddjob.poi;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;

public class DataRowsTest extends TestCase {

	public void testWriteAndReadWithHeadings() throws DataException {
		
		Workbook workbook = new HSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
	
		StyleProvider styleProvider = new DefaultStyleFactory().providerFor(
				workbook);
		
		SheetOut sheetOut = new PoiSheetOut(sheet, styleProvider);
		
		DataRows test = new DataRows();
		test.setFirstRow(2);
		test.setFirstColumn(1);
		test.setWithHeadings(true);
		
		TextCell cell = new TextCell();
		cell.bind(new ValueBinding());
		cell.setTitle("Fruit");
	
		test.setOf(0, cell);
		
		assertEquals(-1, sheetOut.getCurrentRow());
		
		DataWriter writer = test.writerFor(sheetOut);
		
		writer.write("Apples");
		
		assertEquals(3, sheetOut.getCurrentRow());		
		assertEquals(1, cell.getColumn());
		
		Cell titleCell = sheet.getRow(2).getCell(1);
		assertEquals("Fruit", titleCell.getStringCellValue());

		assertEquals(styleProvider.styleFor(DefaultStyleFactory.HEADING_STYLE),
				titleCell.getCellStyle());
				
		assertEquals("Apples", sheet.getRow(3).getCell(1).getStringCellValue());
		assertEquals(3, sheetOut.getCurrentRow());
		
		// second row
		writer.write("Ornages");
		
		assertEquals(4, sheetOut.getCurrentRow());

		assertEquals(4, sheetOut.getCurrentRow());
		assertEquals(1, cell.getColumn());
		
		
		assertEquals(4, test.getLastRow());
		assertEquals(1, test.getLastColumn());
		
		////////////////////////////////
		// Read Side
		
		cell.setValue(null);
		
		SheetIn sheetIn = new PoiSheetIn(sheet);
		
		assertEquals(-1, sheetIn.getCurrentRow());
		
		DataReader reader = test.readerFor(sheetIn);
		
		assertEquals(1, test.getLastColumn());

		
		assertEquals(3, sheetIn.getCurrentRow());

		assertEquals(1, cell.getColumn());
		
		Object result = reader.read();
		
		assertEquals("Apples", cell.getValue());
		assertEquals("Apples", result);
		
		assertEquals(3, sheetIn.getCurrentRow());
		
		result = reader.read();
		
		assertEquals(4, sheetIn.getCurrentRow());
		
		assertEquals("Oranges", cell.getValue());
		
		assertEquals(4, sheetIn.getCurrentRow());
	}
}
