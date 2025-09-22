package dido.poi.data;

import dido.data.DidoData;
import dido.data.immutable.MapData;
import dido.data.immutable.SingleData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.poi.layouts.DataRows;
import dido.poi.layouts.TextCell;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class PoiSheetsTest  {

	@Test
	public void testSheetCreatedAndRead() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();

		
		DataRows rows = new DataRows();
		rows.setSheetName("Fruit");
		
		TextCell cell =  new TextCell();
		
		rows.setOf(0, cell);

		DataOut writer = rows.outTo(workbook);

		DidoData data = MapData.builder()
				.withString("Fruit", "Apples")
				.build();

		writer.accept(data);

		writer.close();
		
		////////////
		// Read Part

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, contains(SingleData.type(String.class).of("Apples")));
		}
	}

	@Test
	public void testSheetCreatedAndReadUnamed() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		TextCell cell =  new TextCell();
		
		DataRows rows = new DataRows();
		rows.setOf(0, cell);


		DataOut writer = rows.outTo(workbook);

		DidoData data = MapData.builder()
				.withString("Fruit", "Apples")
				.build();

		writer.accept(data);
		
		writer.close();
		
		////////////
		// Read Part

		try (DataIn reader = rows.inFrom(workbook)) {

			List<DidoData> results = reader.stream()
					.collect(Collectors.toList());

			assertThat(results, contains(SingleData.type(String.class).of("Apples")));
		}
	}
}                                                       
