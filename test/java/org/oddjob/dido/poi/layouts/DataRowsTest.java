package org.oddjob.dido.poi.layouts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.poi.data.PoiWorkbook;

public class DataRowsTest extends TestCase {

	public void testWriteAndReadWithHeadings() throws DataException, InvalidFormatException, IOException {
		
		ArooaSession session = new StandardArooaSession();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setArooaSession(session);
		workbook.setOutput(new ArooaObject(output));
		
		DataRows test = new DataRows();
		test.setFirstRow(2);
		test.setFirstColumn(3);
		test.setWithHeadings(true);
		
		TextCell cell = new TextCell();
		cell.bind(new DirectBinding());
		cell.setTitle("Fruit");
	
		test.setOf(0, cell);

		DataBook book = new DataBook();
		book.setOf(0, test);
		
		DataWriter writer = book.writerFor(workbook);
		
		writer.write("Apples");
		
		assertEquals(3, test.getLastRow());
		assertEquals(3, test.getLastColumn());
		
		// second row
		writer.write("Oranges");
		
		assertEquals(4, test.getLastRow());
		assertEquals(3, test.getLastColumn());
		
		writer.close();
		
		// Check
		
		Workbook poibook = WorkbookFactory.create(new ByteArrayInputStream(
				output.toByteArray()));
		Cell titleCell = poibook.getSheetAt(0).getRow(1).getCell(2);
		assertEquals("Fruit", titleCell.getStringCellValue());
				
		assertEquals("Apples", poibook.getSheetAt(0).getRow(2).getCell(2).getStringCellValue());
		assertEquals("Oranges", poibook.getSheetAt(0).getRow(3).getCell(2).getStringCellValue());
		
		////////////////////////////////
		// Read Side
		
		book.reset();
		
		assertEquals(0, test.getLastRow());
		assertEquals(0, test.getLastColumn());
		
		DataReader reader = book.readerFor(workbook);
		
		Object result = reader.read();
		
		assertEquals(1, cell.getIndex());
		
		assertEquals(3, test.getLastRow());
		assertEquals(3, test.getLastColumn());
		
		assertEquals("Apples", cell.getValue());
		assertEquals("Apples", result);
		
		result = reader.read();

		assertEquals(4, test.getLastRow());
		assertEquals(3, test.getLastColumn());
		
		assertEquals("Oranges", cell.getValue());
		assertEquals("Oranges", result);
		
		
		assertNull(reader.read());
		
		reader.close();
		
		assertEquals(4, test.getLastRow());
		assertEquals(3, test.getLastColumn());
		
	}
}
