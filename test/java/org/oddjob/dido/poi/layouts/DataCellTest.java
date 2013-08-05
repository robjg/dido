package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.data.PoiWorkbook;

public class DataCellTest extends TestCase {

	
	public void testReference() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		DataCell<String> test = new TextCell();
		test.bind(new DirectBinding());
		
		DataRows rows = new DataRows();
		rows.setFirstColumn(2);
		rows.setFirstRow(10);
		rows.setOf(0, test);
		
		DataBook book = new DataBook();
		book.setOf(0, rows);
		
		DataWriter writer = book.writerFor(workbook);

		writer.write("Apples");
		
		writer.close();
		
		// Read Side
		/////
		
		book.reset();
		
		DataReader reader = book.readerFor(workbook);
		
		assertEquals("Apples", reader.read());
		
		assertEquals("$B$10", test.getReference());
		
		reader.close();
	}	
}
