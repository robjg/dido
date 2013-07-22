package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.data.PoiSheetIn;
import org.oddjob.dido.poi.data.PoiSheetOut;
import org.oddjob.dido.poi.layouts.DataRows;
import org.oddjob.dido.poi.style.DefaultStyleFactory;
import org.oddjob.dido.poi.style.StyleProvider;

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
		cell.bind(new DirectBinding());
		cell.setTitle("Fruit");
	
		test.setOf(0, cell);
		
		assertEquals(-1, sheetOut.getCurrentRow());
		
		DataWriter writer = test.writerFor(sheetOut);
		
		writer.write("Apples");
		
		assertEquals(3, sheetOut.getCurrentRow());		
		assertEquals(1, cell.getIndex());
		
		Cell titleCell = sheet.getRow(2).getCell(1);
		assertEquals("Fruit", titleCell.getStringCellValue());

		assertEquals(styleProvider.styleFor(DefaultStyleFactory.HEADING_STYLE),
				titleCell.getCellStyle());
				
		assertEquals("Apples", sheet.getRow(3).getCell(1).getStringCellValue());
		assertEquals(3, sheetOut.getCurrentRow());
		
		// second row
		writer.write("Oranges");
		
		assertEquals(4, sheetOut.getCurrentRow());

		assertEquals(4, sheetOut.getCurrentRow());
		assertEquals(1, cell.getIndex());
		
		
		assertEquals(4, test.getLastRow());
		assertEquals(1, test.getLastColumn());
		
		writer.close();
		
		////////////////////////////////
		// Read Side
		
		test.reset();
		
		SheetIn sheetIn = new PoiSheetIn(sheet);
		
		assertEquals(-1, sheetIn.getCurrentRow());
		
		DataReader reader = test.readerFor(sheetIn);
		
		Object result = reader.read();
		
		assertEquals(1, cell.getIndex());
		
		assertEquals("Apples", cell.getValue());
		assertEquals("Apples", result);
		
		assertEquals(3, sheetIn.getCurrentRow());
		
		result = reader.read();
		
		assertEquals(4, sheetIn.getCurrentRow());
		
		assertEquals("Oranges", cell.getValue());
		
		assertEquals(4, sheetIn.getCurrentRow());
		
		reader.close();
	}
}
