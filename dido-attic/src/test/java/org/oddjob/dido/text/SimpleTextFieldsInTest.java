package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnIn;

public class SimpleTextFieldsInTest extends TestCase {

	private class OurColumn implements Column {
		
		private String columnLabel;
		private int columnIndex;
		
		public OurColumn(String columnLabel, int columnIndex) {
			this.columnLabel = columnLabel;
			this.columnIndex = columnIndex;
		}
		
		@Override
		public String getLabel() {
			return columnLabel;
		}
		
		@Override
		public int getIndex() {
			return columnIndex;
		}
	}
	
	public void testHeadingsAndColumnHeadings() throws DataException {
				
		String[] headings = { "name", "age", "city" };
		String[] values = { "John", "32", "London" };
		
		SimpleTextFieldsIn test = new SimpleTextFieldsIn();
		
		test.setHeadings(headings);
		test.setValues(values);
		
		ColumnIn<?> nameCol = test.inFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.inFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.inFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.inFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(0, occuCol.getColumnIndex());
		
		assertEquals("John", nameCol.getData());
		assertEquals("32", ageCol.getData());
		assertEquals("London", cityCol.getData());
		assertNull(occuCol.getData());
	}
	
	public void testHeadingsAndColumnHeadingsInDifferentOrder() throws DataException {
		
		String[] headings = { "name", "age", "city" };
		String[] values = { "John", "32", "London" };
		
		SimpleTextFieldsIn test = new SimpleTextFieldsIn();
		
		test.setHeadings(headings);
		test.setValues(values);
		
		ColumnIn<?> cityCol = test.inFor(new OurColumn("city", 0));
		ColumnIn<?> nameCol = test.inFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.inFor(new OurColumn("age", 0));
		ColumnIn<?> occuCol = test.inFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(0, occuCol.getColumnIndex());
		
		assertEquals("John", nameCol.getData());
		assertEquals("32", ageCol.getData());
		assertEquals("London", cityCol.getData());
		assertNull(occuCol.getData());
	}
	
		
	public void testNoHeadings() throws DataException {
		
		SimpleTextFieldsIn test = new SimpleTextFieldsIn();
		
		ColumnIn<?> col1 = test.inFor(new OurColumn(null, 0));
		ColumnIn<?> col2 = test.inFor(new OurColumn(null, 0));
		ColumnIn<?> col3 = test.inFor(new OurColumn(null, 0));
		ColumnIn<?> col4 = test.inFor(new OurColumn(null, 0));
		
		assertEquals(1, col1.getColumnIndex());
		assertEquals(2, col2.getColumnIndex());
		assertEquals(3, col3.getColumnIndex());
		assertEquals(4, col4.getColumnIndex());
		
		String[] values = { "John", "32", "London" };
		test.setValues(values);
		
		assertEquals("John", col1.getData());
		assertEquals("32", col2.getData());
		assertEquals("London", col3.getData());
		assertNull(col4.getData());
	}
	
	public void testColumnHeadingsOnly() {
		
		SimpleTextFieldsIn test = new SimpleTextFieldsIn();

		ColumnIn<?> nameCol = test.inFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.inFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.inFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.inFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(4, occuCol.getColumnIndex());
		
	}
	
	public void testOnlyColumnHeadingsButMoreThanValues() throws DataException {
		
		String[] values = { "John", "32", "London" };
		
		SimpleTextFieldsIn test = new SimpleTextFieldsIn();
		
		ColumnIn<?> nameCol = test.inFor(new OurColumn("name", 0));
		ColumnIn<?> ageCol = test.inFor(new OurColumn("age", 0));
		ColumnIn<?> cityCol = test.inFor(new OurColumn("city", 0));
		ColumnIn<?> occuCol = test.inFor(new OurColumn("occupation", 0));
		
		assertEquals(1, nameCol.getColumnIndex());
		assertEquals(2, ageCol.getColumnIndex());
		assertEquals(3, cityCol.getColumnIndex());
		assertEquals(4, occuCol.getColumnIndex());
		
		test.setValues(values);
		
		assertEquals("John", nameCol.getData());
		assertEquals("32", ageCol.getData());
		assertEquals("London", cityCol.getData());
		
		assertNull(occuCol.getData());
	}
}
