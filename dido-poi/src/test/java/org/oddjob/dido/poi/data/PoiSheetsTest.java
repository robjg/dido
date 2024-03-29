package org.oddjob.dido.poi.data;

import dido.data.GenericData;
import dido.data.MapData;
import dido.how.DataIn;
import dido.how.DataOut;
import org.junit.jupiter.api.Test;
import org.oddjob.dido.poi.layouts.DataRows;
import org.oddjob.dido.poi.layouts.TextCell;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PoiSheetsTest  {

	@Test
	public void testSheetCreatedAndRead() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();

		
		DataRows rows = new DataRows();
		rows.setSheetName("Fruit");
		
		TextCell cell =  new TextCell();
		
		rows.setOf(0, cell);

		DataOut<String> writer = rows.outTo(workbook);

		GenericData<String> data = MapData.newBuilderNoSchema()
				.setString("Fruit", "Apples")
				.build();

		writer.accept(data);

		writer.close();
		
		////////////
		// Read Part

		DataIn<String> reader = rows.inFrom(workbook);
		
		GenericData<String> result = reader.get();
		
		assertThat(result.getStringAt(1), is("Apples"));

		assertThat(reader.get(), nullValue());

		reader.close();
	}

	@Test
	public void testSheetCreatedAndReadUnamed() throws Exception {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		TextCell cell =  new TextCell();
		
		DataRows rows = new DataRows();
		rows.setOf(0, cell);


		DataOut<String> writer = rows.outTo(workbook);

		GenericData<String> data = MapData.newBuilderNoSchema()
				.setString("Fruit", "Apples")
				.build();

		writer.accept(data);
		
		writer.close();
		
		////////////
		// Read Part

		DataIn<String> reader = rows.inFrom(workbook);

		GenericData<String> result = reader.get();

		assertThat(result.getStringAt(1), is("Apples"));
		assertThat(reader.get(), nullValue());
		
		reader.close();
	}
}                                                       
