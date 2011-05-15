package org.oddjob.poi;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

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
		cell.setValue("Apples");
		cell.setTitle("Fruit");
	
		test.setIs(0, cell);
		
		assertEquals(-1, sheetOut.getCurrentRow());
		
		WhereNextOut<SheetOut> nextOut = test.out(sheetOut);
				
		assertEquals(cell, nextOut.getChildren()[0]);
		
		assertEquals(sheetOut, nextOut.getChildData());
				
		assertEquals(3, sheetOut.getCurrentRow());		
		assertEquals(1, cell.getColumn());
		
		Cell titleCell = sheet.getRow(2).getCell(1);
		assertEquals("Fruit", titleCell.getStringCellValue());

		assertEquals(styleProvider.styleFor(DefaultStyleFactory.HEADING_STYLE),
				titleCell.getCellStyle());
		
		cell.out(sheetOut);
		
		assertEquals("Apples", sheet.getRow(3).getCell(1).getStringCellValue());
		assertEquals(3, sheetOut.getCurrentRow());
		
		// second row
		nextOut = test.out(sheetOut);
		
		assertEquals(cell, nextOut.getChildren()[0]);
		
		assertEquals(sheetOut, nextOut.getChildData());
		
		assertEquals(4, sheetOut.getCurrentRow());

		cell.setValue("Oranges");
		cell.out(sheetOut);
		
		test.complete(sheetOut);
		
		assertEquals(4, sheetOut.getCurrentRow());
		assertEquals(1, cell.getColumn());
		
		
		assertEquals(4, test.getLastRow());
		assertEquals(1, test.getLastColumn());
		
		////////////////////////////////
		// Read Side
		
		cell.setValue(null);
		
		SheetIn sheetIn = new PoiSheetIn(sheet);
		
		assertEquals(-1, sheetIn.getCurrentRow());
		
		WhereNextIn<SheetIn> nextIn = test.in(sheetIn);
		
		assertEquals(1, test.getLastColumn());

		
		assertEquals(cell, nextIn.getChildren()[0]);
		
		assertEquals(sheetIn, nextIn.getChildData());

		assertEquals(3, sheetIn.getCurrentRow());

		assertEquals(1, cell.getColumn());
		
		cell.in(sheetIn);
		
		assertEquals("Apples", cell.getValue());
		
		assertEquals(3, sheetIn.getCurrentRow());
		
		nextIn = test.in(sheetIn);
		
		assertEquals(cell, nextIn.getChildren()[0]);
		
		assertEquals(sheetIn, nextIn.getChildData());

		assertEquals(4, sheetIn.getCurrentRow());
		
		cell.in(sheetIn);
		assertEquals("Oranges", cell.getValue());
		
		nextIn = test.in(sheetIn);
		assertNull(nextIn);
		
		test.complete(sheetIn);
		
		assertEquals(4, sheetIn.getCurrentRow());
	}
}
