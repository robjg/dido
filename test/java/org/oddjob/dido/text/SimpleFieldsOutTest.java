package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnOut;

public class SimpleFieldsOutTest extends TestCase {

	private class OurColumn implements Column {
	
		private String columnLabel;
		private int columnIndex;
		
		public OurColumn(String columnLabel, int columnIndex) {
			this.columnLabel = columnLabel;
			this.columnIndex = columnIndex;
		}
		
		@Override
		public String getColumnLabel() {
			return columnLabel;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
	}
	
	public void testNoHeadingsNoHeadings() throws DataException {
		
		FieldsOut test = new SimpleFieldsOut();
		
		ColumnOut<?> nameCol = test.columnOutFor(new OurColumn(null, 0));
		ColumnOut<?> ageCol = test.columnOutFor(new OurColumn(null, 0));
		ColumnOut<?> cityCol = test.columnOutFor(new OurColumn(null, 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		
	}
	
	public void testHeadingsWithNoHeadings() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		ColumnOut<?> ageCol = test.columnOutFor(new OurColumn("age", 0));
		ColumnOut<?> cityCol = test.columnOutFor(new OurColumn("city", 0));
		ColumnOut<?> nameCol = test.columnOutFor(new OurColumn("name", 4));
		
		assertEquals(1, ageCol.getColumnIndex());
		assertEquals(2, cityCol.getColumnIndex());
		assertEquals(4, nameCol.getColumnIndex());
				
		String[] headings = test.headings();
		
		assertEquals("age", headings[0]);
		assertEquals("city", headings[1]);
		assertEquals(null, headings[2]);
		assertEquals("name", headings[3]);
		
	}
	
	public void testHeadingsWithHeadings() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		ColumnOut<?> ageCol = test.columnOutFor(new OurColumn("age", 0));
		ColumnOut<?> cityCol = test.columnOutFor(new OurColumn("city", 0));
		ColumnOut<?> nameCol = test.columnOutFor(new OurColumn("name", 4));
		
		assertEquals(1, ageCol.getColumnIndex());
		assertEquals(2, cityCol.getColumnIndex());
		assertEquals(4, nameCol.getColumnIndex());
		
		String[] results = test.headings();
		
		assertEquals("age", results[0]);
		assertEquals("city", results[1]);
		assertEquals(null, results[2]);
		assertEquals("name", results[3]);
	}
	
	public void testNextOut() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		assertEquals(false, test.isWrittenTo());
		
		ColumnOut<String> nameCol = test.columnOutFor(new OurColumn(null, 1));
		ColumnOut<String> ageCol = test.columnOutFor(new OurColumn(null, 2));
		ColumnOut<String> cityCol = test.columnOutFor(new OurColumn(null, 3));
		
		nameCol.setColumnData("John");
		ageCol.setColumnData("34");
		cityCol.setColumnData("London");
		
		assertEquals(true, test.isWrittenTo());
		
		String[] results = test.values();
		
		assertEquals("John", results[0]);
		assertEquals("34", results[1]);
		assertEquals("London", results[2]);
	}
	
	public void testRandomColumn() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		ColumnOut<String> nameCol = test.columnOutFor(new OurColumn(null, 5));
		ColumnOut<String> ageCol = test.columnOutFor(new OurColumn(null, 3));
		ColumnOut<String> cityCol = test.columnOutFor(new OurColumn(null, 6));
				
		nameCol.setColumnData("John");
		ageCol.setColumnData("34");
		cityCol.setColumnData("London");
		
		String[] results = test.values();
		
		assertEquals(null, results[0]);
		assertEquals(null, results[1]);
		assertEquals("34", results[2]);
		assertEquals(null, results[3]);
		assertEquals("John", results[4]);
		assertEquals("London", results[5]);
	}
	
	public void testNamedFields() throws DataException {
				
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		ColumnOut<String> nameCol = test.columnOutFor(new OurColumn(null, 4));
		ColumnOut<String> ageCol = test.columnOutFor(new OurColumn(null, 1));
		ColumnOut<String> cityCol = test.columnOutFor(new OurColumn(null, 2));
				
		nameCol.setColumnData("John");
		ageCol.setColumnData("34");
		cityCol.setColumnData("London");
		
		String[] results = test.values();
		
		assertEquals("34", results[0]);
		assertEquals("London", results[1]);
		assertEquals(null, results[2]);
		assertEquals("John", results[3]);
	}
}
