package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import dido.data.MapData;
import dido.pickles.DataIn;
import dido.pickles.DataOut;
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
		cell.setBinding(new DirectBinding());
		cell.setLabel("Fruit");
	
		test.setOf(0, cell);

		DataOut<String> writer = test.outTo(workbook);

		GenericData<String> data1 = MapData.newBuilderNoSchema()
						.setString("Fruit", "Apples")
								.build();
		GenericData<String> data2 = MapData.newBuilderNoSchema()
				.setString("Fruit", "Oranges")
				.build();

		writer.accept(data1);
		
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
