package org.oddjob.poi;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;

public class DataCellTest extends TestCase {

	
	public void testReference() throws DataException {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		assertEquals(0, sheet.getLastRowNum());
		sheet.createRow(10);
		assertEquals(10, sheet.getLastRowNum());
		
		DataCell<String> test = new TextCell();
		test.bind(new ValueBinding());
		
		SheetOut out = new PoiSheetOut(sheet);
		
		DataWriter writer = test.writerFor(out);

		writer.write("Apples");
		
		SheetIn in = new PoiSheetIn(sheet);
		assertTrue(in.nextRow());

		DataReader reader = test.readerFor(in);
		
		assertEquals("Apples", reader.read());
		
		assertEquals("$A$1", test.getReference());
	}
	
}
