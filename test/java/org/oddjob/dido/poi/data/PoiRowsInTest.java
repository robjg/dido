package org.oddjob.dido.poi.data;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.poi.CellIn;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.TupleIn;

public class PoiRowsInTest extends TestCase {

	Sheet sheet;
	
	private class OurSheetIn implements SheetIn {
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type)
				throws DataException {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public Sheet getTheSheet() {
			return sheet;
		}
	}

	private class OurColumn implements Field {
		
		@Override
		public String getLabel() {
			return "Quantity";
		}
	}	
	
	public void testCreateCellInWithHeader() throws DataException {
		
		Workbook workbook = new XSSFWorkbook();
		sheet = workbook.createSheet();

		Row row = sheet.createRow(2);
		row.createCell(6).setCellValue("Fruit");
		row.createCell(7).setCellValue("Quantity");
		row.createCell(8).setCellValue("Price");
		
		PoiRowsIn test = new PoiRowsIn(new OurSheetIn(), 3, 7);
		
		assertEquals(2, test.getLastRow());
		assertEquals(6, test.getLastColumn());
		
		assertTrue(test.headerRow());
		
		assertEquals(3, test.getLastRow());
		assertEquals(6, test.getLastColumn());
		
		TupleIn tupleIn = test.provideDataIn(TupleIn.class);
		
		CellIn<?> cellIn = tupleIn.inFor(new OurColumn());

		assertEquals(2, cellIn.getColumnIndex());
		assertEquals(8, test.getLastColumn());
		
		row = sheet.createRow(3);
		row.createCell(6).setCellValue("Apple");
		row.createCell(7).setCellValue(17);
		row.createCell(8).setCellValue(24.3);
		
		assertTrue(test.nextRow());
		
		assertEquals(17.0, cellIn.getData());
		
		assertEquals(4, test.getLastRow());
		assertEquals(8, test.getLastColumn());
	}
}
