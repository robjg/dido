package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnIn;

public class SimpleFieldsInTest extends TestCase {

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
	
	public void testHeadingsAndColumnHeadings() throws DataException {
				
		String[] headings = { "name", "age", "city" };
		String[] values = { "John", "32", "London" };
		
		SimpleFieldsIn test = new SimpleFieldsIn();
		
		test.setHeadings(headings);
		test.setValues(values);
		
		ColumnIn<?> nameCol = test.columnInFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.columnInFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.columnInFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.columnInFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(0, occuCol.getColumnIndex());
		
		assertEquals("John", nameCol.getColumnData());
		assertEquals("32", ageCol.getColumnData());
		assertEquals("London", cityCol.getColumnData());
		assertNull(occuCol.getColumnData());
	}
	
	public void testHeadingsAndColumnHeadingsInDifferentOrder() throws DataException {
		
		String[] headings = { "name", "age", "city" };
		String[] values = { "John", "32", "London" };
		
		SimpleFieldsIn test = new SimpleFieldsIn();
		
		test.setHeadings(headings);
		test.setValues(values);
		
		ColumnIn<?> cityCol = test.columnInFor(new OurColumn("city", 0));
		ColumnIn<?> nameCol = test.columnInFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.columnInFor(new OurColumn("age", 0));
		ColumnIn<?> occuCol = test.columnInFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(0, occuCol.getColumnIndex());
		
		assertEquals("John", nameCol.getColumnData());
		assertEquals("32", ageCol.getColumnData());
		assertEquals("London", cityCol.getColumnData());
		assertNull(occuCol.getColumnData());
	}
	
		
	public void testNoHeadings() throws DataException {
		
		SimpleFieldsIn test = new SimpleFieldsIn();
		
		ColumnIn<?> col1 = test.columnInFor(new OurColumn(null, 0));
		ColumnIn<?> col2 = test.columnInFor(new OurColumn(null, 0));
		ColumnIn<?> col3 = test.columnInFor(new OurColumn(null, 0));
		ColumnIn<?> col4 = test.columnInFor(new OurColumn(null, 0));
		
		assertEquals(1, col1.getColumnIndex());
		assertEquals(2, col2.getColumnIndex());
		assertEquals(3, col3.getColumnIndex());
		assertEquals(4, col4.getColumnIndex());
		
		String[] values = { "John", "32", "London" };
		test.setValues(values);
		
		assertEquals("John", col1.getColumnData());
		assertEquals("32", col2.getColumnData());
		assertEquals("London", col3.getColumnData());
		assertNull(col4.getColumnData());
	}
	
	public void testColumnHeadingsOnly() {
		
		SimpleFieldsIn test = new SimpleFieldsIn();

		ColumnIn<?> nameCol = test.columnInFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.columnInFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.columnInFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.columnInFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(4, occuCol.getColumnIndex());
		
	}
	
	public void testOnlyColumnHeadingsButMoreThanValues() throws DataException {
		
		String[] values = { "John", "32", "London" };
		
		SimpleFieldsIn test = new SimpleFieldsIn();
		
		ColumnIn<?> nameCol = test.columnInFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.columnInFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.columnInFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.columnInFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(4, occuCol.getColumnIndex());
		
		test.setValues(values);
		
		assertEquals("John", nameCol.getColumnData());
		assertEquals("32", ageCol.getColumnData());
		assertEquals("London", cityCol.getColumnData());
		
		assertNull(occuCol.getColumnData());
	}
}
