package org.oddjob.dido.poi.layouts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.data.PoiBookIn;
import org.oddjob.dido.poi.data.PoiBookOut;
import org.oddjob.dido.poi.layouts.DataRows;
import org.oddjob.dido.poi.layouts.DataSheet;

public class DataSheetTest extends TestCase {

	public void testWriteAndRead() throws DataException, InvalidFormatException, IOException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		BookOut bookOut = new PoiBookOut(output);
		
		DataSheet test = new DataSheet();
		
		DataRows child = new DataRows();
		test.setOf(0, child);
		
		DataWriter writer = test.writerFor(bookOut);
		
		writer.write(new Object());
		
		bookOut.close();
		
		BookIn bookIn = new PoiBookIn(new ByteArrayInputStream(		
					output.toByteArray()));
		
		DataReader reader = test.readerFor(bookIn);
		
		assertNull(reader.read());

	}
}
