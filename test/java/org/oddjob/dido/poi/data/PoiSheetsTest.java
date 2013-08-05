package org.oddjob.dido.poi.data;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.layouts.DataBook;
import org.oddjob.dido.poi.layouts.DataRows;
import org.oddjob.dido.poi.layouts.TextCell;

public class PoiSheetsTest extends TestCase {

	public void testSheetCreatedAndRead() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		BookOut bookOut = workbook.provideDataOut(BookOut.class);
		
		Sheet sheet1 = bookOut.createSheet("Fruit");

		SheetOut test1 = new PoiSheetOut(sheet1);
		
		
		DataRows rows = new DataRows();
		
		TextCell cell =  new TextCell();
		
		rows.setOf(0, cell);
		
		cell.bind(new DirectBinding());
		
		
		DataWriter writer = rows.writerFor(test1);
		
		writer.write("Apples");
		
		writer.close();
		
		////////////
		// Read Part

		rows.reset();

		assertNull(cell.value());
		
		BookIn bookIn = workbook.provideDataIn(BookIn.class);
		
		Sheet sheet2 = bookIn.getSheet("Fruit");
		
		SheetIn test2 = new PoiSheetIn(sheet2);
		
		DataReader reader = rows.readerFor(test2);
		
		Object result = reader.read();
		
		assertEquals("Apples", result);
		
		assertEquals(null, reader.read());
		
		reader.close();
	}
	
	public void testSheetCreatedAndReadUnamed() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		TextCell cell =  new TextCell();
		
		DataRows rows = new DataRows();
		rows.setOf(0, cell);
		
		DataBook book = new DataBook();
		book.setOf(0, rows);
		
		cell.bind(new DirectBinding());
		
		DataWriter writer = book.writerFor(workbook);
		
		writer.write("Apples");
		
		writer.close();
		
		////////////
		// Read Part

		rows.reset();

		assertNull(cell.value());
		
		DataReader reader = book.readerFor(workbook);
		
		Object result = reader.read();
		
		assertEquals("Apples", result);
		
		assertEquals(null, reader.read());
		
		reader.close();
	}
}                                                       
