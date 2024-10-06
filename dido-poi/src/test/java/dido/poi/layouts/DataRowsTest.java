package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.MapData;
import dido.data.NamedData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.data.PoiWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataRowsTest {

	@Test
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

		DataOut writer = test.outTo(workbook);

		DidoData data1 = MapData.newBuilderNoSchema()
						.withString("Fruit", "Apples")
								.build();
		DidoData data2 = MapData.newBuilderNoSchema()
				.withString("Fruit", "Oranges")
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

		DataIn<NamedData> reader = test.inFrom(workbook);

		NamedData result = reader.get();
		
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
