package org.oddjob.dido.poi.layouts;

import dido.data.GenericData;
import dido.data.MapData;
import dido.how.DataIn;
import dido.how.DataOut;
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.poi.data.PoiWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DataRowsTest extends TestCase {

	public void testWriteAndReadWithHeadings() throws Exception {
		
		ArooaSession session = new StandardArooaSession();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PoiWorkbook workbook = new PoiWorkbook();
		workbook.setOutput(output);
		
		DataRows test = new DataRows();
		test.setFirstRow(2);
		test.setFirstColumn(3);
		test.setWithHeader(true);
		
		TextCell cell = new TextCell();
		cell.setName("Fruit");
	
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

		// second row
		writer.accept(data2);
		
		assertEquals(4, test.getLastRow());

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
		
//		assertEquals(0, test.getLastRow());
//		assertEquals(0, test.getLastColumn());

		DataIn<String> reader = test.inFrom(workbook);

		GenericData<String> result = reader.get();
		
		assertEquals(3, test.getLastRow());

		assertEquals("Apples", result.getString("Fruit"));

		result = reader.get();

		assertEquals(4, test.getLastRow());

		assertEquals("Oranges", result.getString("Fruit"));

		assertNull(reader.get());
		
		reader.close();
		
		assertEquals(4, test.getLastRow());
	}
}
