package org.oddjob.dido.poi.data;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.poi.CellOut;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.tabular.Column;

public class PoiRowsOutTest extends TestCase {

	Sheet sheet;
	
	private class OurSheetOut implements SheetOut {
		
		@Override
		public <T extends DataOut> T provideDataOut(Class<T> type)
				throws DataException {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public Sheet getTheSheet() {
			return sheet;
		}

		@Override
		public boolean isWrittenTo() {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public CellStyle styleFor(String styleName) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void close() {
			throw new RuntimeException("Unexpected.");
		}
	}

	private class OurColumn implements Column {
		
		@Override
		public int getIndex() {
			return 2;
		}
		
		@Override
		public String getLabel() {
			return "Quantity";
		}
	}	
	
	public void testCreateCellInWithHeader() throws DataException {
		
		Workbook workbook = new XSSFWorkbook();
		sheet = workbook.createSheet();

		
		PoiRowsOut test = new PoiRowsOut(new OurSheetOut(), 3, 7);
		
		assertEquals(2, test.getLastRow());
		assertEquals(6, test.getLastColumn());
		
		TupleOut tupleOut = test.provideDataOut(TupleOut.class);
		test.headerRow(null);
		
		@SuppressWarnings("unchecked")
		CellOut<Object> cellOut = (CellOut<Object>) tupleOut.outFor(new OurColumn());

		assertEquals(2, cellOut.getColumnIndex());
		
		assertEquals(3, test.getLastRow());
		assertEquals(8, test.getLastColumn());

		Row row = sheet.getRow(2);
		assertEquals("Quantity", row.getCell(7).getStringCellValue());
		
		test.nextRow();
		cellOut.setData(new Double(17));
		
		row = sheet.getRow(3);
		assertEquals(17.0, (double) row.getCell(7).getNumericCellValue());
		
		assertEquals(4, test.getLastRow());
		assertEquals(8, test.getLastColumn());
	}
}
