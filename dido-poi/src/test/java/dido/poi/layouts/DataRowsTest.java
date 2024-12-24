package dido.poi.layouts;

import dido.data.DidoData;
import dido.data.MapData;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

		DidoData data1 = MapData.builderNoSchema()
						.withString("Fruit", "Apples")
								.build();
		DidoData data2 = MapData.builderNoSchema()
				.withString("Fruit", "Oranges")
				.build();

		writer.accept(data1);
		writer.accept(data2);
		
		writer.close();

		assertEquals(4, test.getLastRow());

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

		try (DataIn reader = test.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, contains(
					MapData.of("Fruit", "Apples"),
					MapData.of("Fruit","Oranges")));
		}

		assertEquals(4, test.getLastRow());
	}
}
