package org.oddjob.dido.poi.layouts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.bio.DirectBinding;
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
		test.setSheetName("Fruit");
		
		DataRows rows = new DataRows();
		test.setOf(0, rows);
		
		TextCell text = new TextCell();
		rows.setOf(0, text);
		
		text.setBinding(new DirectBinding());
		
		bookOut.createSheet("Decoy");
		
		// Write Side
		/////////////
		
		DataWriter writer = test.writerFor(bookOut);
		
		writer.write("Apple");
		
		writer.close();
		bookOut.close();
		
		// Read Side
		////////////
		
		test.reset();
		
		workbook.setInput(new ArooaObject(new ByteArrayInputStream(		
					output.toByteArray())));
		
		BookIn bookIn = workbook.provideDataIn(BookIn.class);
		
		DataReader reader = test.readerFor(bookIn);
		
		assertEquals("Apple", reader.read());
		
		assertNull(reader.read());
		
		reader.close();
	}

	public void testWriteAndReadAsTopLevelLayout() throws DataException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(new StandardArooaSession());
		workbook.setOutput(new ArooaObject(output));
		
		DataSheet test = new DataSheet();
		test.setSheetName("Fruit");
		
		DataRows rows = new DataRows();
		test.setOf(0, rows);
		
		TextCell text = new TextCell();
		rows.setOf(0, text);
		
		text.setBinding(new DirectBinding());
		
		// Write Side
		/////////////
		
		DataWriter writer = test.writerFor(workbook);
		
		writer.write("Apple");
		
		writer.close();
		
		// Read Side
		////////////
		
		test.reset();
		
		workbook.setInput(new ArooaObject(new ByteArrayInputStream(		
					output.toByteArray())));
		
		DataReader reader = test.readerFor(workbook);
		
		assertEquals("Apple", reader.read());
		
		assertNull(reader.read());

		reader.close();
	}
	
	private class OurBookIn implements BookIn {
		
		@Override
		public Sheet getSheet(String sheetName) {
			return null;
		}
		
		@Override
		public Sheet nextSheet() {
			return null;
		}
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type)
				throws DataException {
			return type.cast(this);
		}
	}
	
	public void testReadWhenNoSheet() throws DataException {
		
		DataSheet test = new DataSheet();
		
		DataReader reader = test.readerFor(new OurBookIn());
		
		assertNull(reader.read());
		
		reader.close();
		
		test.reset();
		test.setSheetName("Fruit");
		
		reader = test.readerFor(new OurBookIn());
		
		assertNull(reader.read());
		
		reader.close();
	}
	
	private class OurLayout implements Layout {

		String toRead;
		
		String written;
		
		boolean closed;
		
		boolean reset;
		
		@Override
		public DataReader readerFor(DataIn dataIn) throws DataException {
			return new DataReader() {
				
				@Override
				public Object read() throws DataException {
					try {
						return toRead;
					}
					finally {
						toRead = null;
					}
				}
				
				@Override
				public void close() throws DataException {
					closed = true;
				}
			};
		}

		@Override
		public DataWriter writerFor(DataOut dataOut) throws DataException {
			return new DataWriter() {
				
				@Override
				public boolean write(Object object) throws DataException {
					written = (String) object;
					// keep open like rows does.
					return true;
				}
				
				@Override
				public void close() throws DataException {
					closed = true;
				}
			};
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void setBinding(Binding bindings) {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public Iterable<Layout> childLayouts() {
			return new ArrayList<Layout>();
		}

		@Override
		public void reset() {
			reset = true;
			toRead = null;
			written = null;
			closed = false;
		}
	}
	
	public void testWriteAndReadMultipleSheets() 
	throws DataException, InvalidFormatException, IOException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(new StandardArooaSession());
		workbook.setOutput(new ArooaObject(output));
	
		DataBook dataBook = new DataBook();
		
		DataSheet test1 = new DataSheet();
		test1.setSheetName("Fruit");
		
		OurLayout layout1 = new OurLayout();

		test1.setOf(0, layout1);
		
		DataSheet test2 = new DataSheet();
		test2.setSheetName("Vegtables");
		
		OurLayout layout2 = new OurLayout();

		test2.setOf(0, layout2);
		
		dataBook.setOf(0, test1);
		dataBook.setOf(1, test2);
		
		// Write Side
		/////////////
		
		DataWriter writer = dataBook.writerFor(workbook);
		
		writer.write("Apple");
		
		assertEquals("Apple", layout1.written);
		
		// Note that when writing this will never be given an object because 
		// the first writer is still open.
		assertEquals(null, layout2.written);
		
		writer.close();
		
		assertEquals(true, layout1.closed);
		assertEquals(true, layout2.closed);
		
		// Read Side
		////////////
		
		dataBook.reset();
		
		assertEquals(true, layout1.reset);
		assertEquals(true, layout2.reset);
		
		layout1.toRead = "Apple";
		layout2.toRead = "Pear";
		
		workbook.setInput(new ArooaObject(new ByteArrayInputStream(		
					output.toByteArray())));
		
		DataReader reader = dataBook.readerFor(workbook);
		
		assertEquals("Apple", reader.read());
		assertEquals("Pear", reader.read());
		
		assertNull(reader.read());
		
		reader.close();
		
		assertEquals(true, layout1.closed);
		assertEquals(true, layout2.closed);
	}

}
