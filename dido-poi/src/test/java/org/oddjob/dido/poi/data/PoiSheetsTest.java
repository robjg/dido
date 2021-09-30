package org.oddjob.dido.poi.data;

import dido.data.GenericData;
import dido.data.MapData;
import dido.pickles.DataIn;
import dido.pickles.DataOut;
import org.junit.jupiter.api.Test;
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

		DataOut<String> writer = rows.outTo(workbook);

		GenericData<String> data = MapData.newBuilderNoSchema()
				.setString("Fruit", "Apples")
				.build();

		writer.accept(data);

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


		DataOut<String> writer = rows.outTo(workbook);

		GenericData<String> data = MapData.newBuilderNoSchema()
				.setString("Fruit", "Apples")
				.build();

		writer.accept(data);
		
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
