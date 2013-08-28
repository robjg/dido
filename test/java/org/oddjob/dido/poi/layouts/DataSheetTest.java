package org.oddjob.dido.poi.layouts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.data.PoiWorkbook;

public class DataSheetTest extends TestCase {

	public void testWriteAndRead() throws DataException, InvalidFormatException, IOException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(new StandardArooaSession());
		workbook.setOutput(new ArooaObject(output));
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		
		DataSheet test = new DataSheet();
		
		DataRows child = new DataRows();
		test.setOf(0, child);
		
		DataWriter writer = test.writerFor(bookOut);
		
		writer.write(new Object());
		
		bookOut.close();
		
		workbook.setInput(new ArooaObject(new ByteArrayInputStream(		
					output.toByteArray())));
		
		BookIn bookIn = workbook.provideDataIn(BookIn.class);
		
		DataReader reader = test.readerFor(bookIn);
		
		assertNull(reader.read());

	}
}
