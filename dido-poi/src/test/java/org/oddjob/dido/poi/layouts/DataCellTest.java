package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;

import org.apache.poi.ss.usermodel.CellStyle;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.poi.CellIn;
import org.oddjob.dido.poi.CellOut;
import org.oddjob.dido.poi.TupleIn;
import org.oddjob.dido.poi.TupleOut;
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.tabular.Column;

public class DataCellTest extends TestCase {

	private String data;
	
	private class OurCellOut implements CellOut<String> {

		@Override
		public void setData(String data) throws DataException {
			DataCellTest.this.data = data;
		}

		@Override
		public Class<?> getType() {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public int getColumnIndex() {
			return 3;
		}

		@Override
		public String getCellReference() {
			return "A3";
		}
	}
	
	private class OurTupleOut implements TupleOut {
		
		@Override
		public CellOut<?> outFor(Field column) {
			assertEquals(0, ((Column) column).getIndex());
			assertEquals("Fruit", column.getLabel());
			
			return new OurCellOut();
		}
		
		@Override
		public <T extends DataOut> T provideDataOut(Class<T> type)
				throws DataException {
			assertEquals(TupleOut.class, type);
			return type.cast(this);
		}

		@Override
		public boolean isWrittenTo() {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public CellStyle styleFor(String styleName) {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public void testWrite() throws DataException {
		
		DataCell<String> test = new TextCell();
		test.setBinding(new DirectBinding());
		test.setLabel("Fruit");
		
		OurTupleOut tupleOut = new OurTupleOut();
		
		DataWriter writer = test.writerFor(tupleOut);
		
		assertFalse(writer.write("Apple"));
		
		writer.close();

		assertEquals("Apple", data);
		
		writer = test.writerFor(tupleOut);
		
		assertFalse(writer.write("Pear"));
		
		writer.close();
		
		assertEquals("Pear", data);
		
		assertEquals("A3", test.getReference());
		assertEquals(3, test.getIndex());
		
	}
	
	private class OurCellIn implements CellIn<String> {

		@Override
		public String getData() throws DataException {
			return data;
		}

		@Override
		public Class<?> getType() {
			throw new RuntimeException("Unexpected!");
		}

		@Override
		public int getColumnIndex() {
			return 4;
		}

		@Override
		public String getCellReference() {
			return "B4";
		}
		
	}
	
	private class OurTupleIn implements TupleIn {

		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type)
				throws DataException {
			assertEquals(TupleIn.class, type);
			return type.cast(this);
		}

		@Override
		public CellIn<?> inFor(Field column) {
			
			assertEquals(0, ((Column) column).getIndex());
			assertEquals("Fruit", column.getLabel());
			
			return new OurCellIn();
		}
		
	}
	
	
	public void testRead() throws DataException {
		
		DataCell<String> test = new TextCell();
		test.setBinding(new DirectBinding());
		test.setLabel("Fruit");
		
		OurTupleIn tupleIn = new OurTupleIn();
		
		DataReader reader = test.readerFor(tupleIn);
		
		data = "Apple";
		
		assertEquals("Apple", reader.read());
		
		assertEquals(null, reader.read());
		
		reader.close();

		data = "Pear";
		
		reader = test.readerFor(tupleIn);
		
		assertEquals("Pear", reader.read());
		
		// we should really read twice but close negates the need to.
		
		reader.close();

		data = null;
		
		// we should really create a new reader but close resets the reader.
		
		assertEquals(null, reader.read());
		
		reader.close();
		
		assertEquals("B4", test.getReference());
		assertEquals(4, test.getIndex());
	}
	
	
	public void testReference() throws DataException {
		
		PoiWorkbook workbook = new PoiWorkbook();
		
		DataCell<String> test = new TextCell();
		test.setBinding(new DirectBinding());
		
		DataRows rows = new DataRows();
		rows.setFirstColumn(2);
		rows.setFirstRow(10);
		rows.setOf(0, test);
		
		DataBook book = new DataBook();
		book.setOf(0, rows);
		
		DataWriter writer = book.writerFor(workbook);

		writer.write("Apples");
		
		writer.close();
		
		// Read Side
		/////
		
		book.reset();
		
		DataReader reader = book.readerFor(workbook);
		
		assertEquals("Apples", reader.read());
		
		assertEquals("$B$10", test.getReference());
		
		reader.close();
	}	
}
