package org.oddjob.dido.tabular;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnHelper;

public class ColumnHelperTest extends TestCase {

	public void testToArray() {
		
		Map<Integer, String> things = new HashMap<Integer, String>();
		
		String[] result = ColumnHelper.toArray(things);

		assertEquals(0, result.length);

		things.put(3, "Blue");
		
		result = ColumnHelper.toArray(things);
		
		assertEquals("[null, null, Blue]", Arrays.toString(result));
		
		things.put(5, "Apple");
		
		result = ColumnHelper.toArray(things);
		
		assertEquals("[null, null, Blue, null, Apple]", Arrays.toString(result));
		
		things.put(1, "A");
		things.put(2, "Large");
		things.put(4, "Square");
		
		result = ColumnHelper.toArray(things);
		
		assertEquals("[A, Large, Blue, Square, Apple]", Arrays.toString(result));
	}
	
	public void testToArrayWithIndexLessThan1() {
		
		Map<Integer, String> things = new HashMap<Integer, String>();
		
		things.put(0, "Blue");
		things.put(-1, "Green");
		
		String[] result = ColumnHelper.toArray(things);
		
		assertEquals("[]", Arrays.toString(result));
	}
	
	
	private class OurColumn implements Column {
		
		private final String columnLabel;
		private final int columnIndex;
		
		public OurColumn(String columnLabel, int columnIndex) {
			this.columnLabel = columnLabel;
			this.columnIndex = columnIndex;
		}
		
		public OurColumn(int columnIndex) {
			this(null, columnIndex);
		}
		
		public OurColumn() {
			this(0);
		}
		
		public OurColumn(String columnLabel) {
			this(columnLabel, 0);
		}
		
		@Override
		public String getLabel() {
			return columnLabel;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
	}
	
	public void testWithNoHeadingsAndNoIndexes() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(1, test.columnIndexFor(new OurColumn()));
		assertEquals(2, test.columnIndexFor(new OurColumn()));
		assertEquals(3, test.columnIndexFor(new OurColumn()));
		
		assertEquals(3, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[]", Arrays.toString(test.getHeadings()));
	}
	
	public void testWithNoHeadingAndSomeIndexes() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(1, test.columnIndexFor(new OurColumn()));
		assertEquals(4, test.columnIndexFor(new OurColumn(4)));
		assertEquals(5, test.columnIndexFor(new OurColumn()));
		
		assertEquals(5, test.getLastColumn());
		assertEquals(5, test.getMaxColumn());
		
		assertEquals("[]", Arrays.toString(test.getHeadings()));
	}
	
	public void testWithMessedUpIndexes() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(3, test.columnIndexFor(new OurColumn(3)));
		assertEquals(2, test.columnIndexFor(new OurColumn(2)));
		assertEquals(3, test.columnIndexFor(new OurColumn()));
		
		assertEquals(3, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[]", Arrays.toString(test.getHeadings()));
	}
	
	public void testWithLabels() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(1, test.columnIndexFor(new OurColumn("Fruit")));
		assertEquals(2, test.columnIndexFor(new OurColumn("Colour")));
		assertEquals(3, test.columnIndexFor(new OurColumn("Quantity")));
		
		assertEquals(3, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[Fruit, Colour, Quantity]", Arrays.toString(test.getHeadings()));
	}
	
	public void testWithLabelsAndIndexsButNoHeadings() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(2, test.columnIndexFor(new OurColumn("Colour", 2)));
		assertEquals(3, test.columnIndexFor(new OurColumn("Quantity", 3)));
		assertEquals(1, test.columnIndexFor(new OurColumn("Fruit", 1)));
		
		assertEquals(1, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[Fruit, Colour, Quantity]", Arrays.toString(test.getHeadings()));
	}
	
	
	public void testWithLabelsButNoIndexsAndNoHeadings() {
		
		ColumnHelper test = new ColumnHelper();
		
		assertEquals(1, test.columnIndexFor(new OurColumn("Colour")));
		assertEquals(2, test.columnIndexFor(new OurColumn("Quantity")));
		assertEquals(3, test.columnIndexFor(new OurColumn("Fruit")));
		
		assertEquals(3, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[Colour, Quantity, Fruit]", Arrays.toString(test.getHeadings()));
	}
	
	public void testWithLabelsAndHeadings() {
		
		ColumnHelper test = new ColumnHelper();
		
		test.setHeadings(new String[] { "Fruit", "Colour", "Quantity" });
		
		assertEquals(3, test.columnIndexFor(new OurColumn("Quantity")));
		assertEquals(1, test.columnIndexFor(new OurColumn("Fruit")));
		assertEquals(2, test.columnIndexFor(new OurColumn("Colour")));
		assertEquals(0, test.columnIndexFor(new OurColumn("Price")));
		
		assertEquals(2, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[Fruit, Colour, Quantity]", Arrays.toString(test.getHeadings()));
	}
	
	public void testIndexsIgnoredWithLabelsAndHeadings() {
		
		ColumnHelper test = new ColumnHelper();
		
		test.setHeadings(new String[] { "Fruit", "Colour", "Quantity" });
		
		assertEquals(3, test.columnIndexFor(new OurColumn("Quantity", 55)));
		assertEquals(1, test.columnIndexFor(new OurColumn("Fruit", 41)));
		assertEquals(2, test.columnIndexFor(new OurColumn("Colour", 32)));
		assertEquals(0, test.columnIndexFor(new OurColumn("Price", 47)));
		
		assertEquals(2, test.getLastColumn());
		assertEquals(3, test.getMaxColumn());
		
		assertEquals("[Fruit, Colour, Quantity]", Arrays.toString(test.getHeadings()));
	}
}
