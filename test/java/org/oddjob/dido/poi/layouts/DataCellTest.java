package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.data.PoiSheetIn;
import org.oddjob.dido.poi.data.PoiSheetOut;
import org.oddjob.dido.poi.layouts.DataCell;

public class DataCellTest extends TestCase {

	
	public void testReference() throws DataException {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		
		DataCell<String> test = new TextCell();
		test.bind(new DirectBinding());
		
		PoiSheetOut out = new PoiSheetOut(sheet);
		out.startAt(10, 2);
		out.nextRow();
		
		DataWriter writer = test.writerFor(out);

		writer.write("Apples");
		
		PoiSheetIn in = new PoiSheetIn(sheet);
		in.startAt(10, 2);
		
		assertTrue(in.nextRow());

		DataReader reader = test.readerFor(in);
		
		assertEquals("Apples", reader.read());
		
		assertEquals("$C$11", test.getReference());
	}
	
}
