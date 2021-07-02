package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.field.Field;

public class FixedWidthTextFieldHelperTest extends TestCase {

	private class OurFixedWidthColumn extends OurField 
	implements FixedWidthColumn {
		
		private final int index;
		private final int length;
		
		public OurFixedWidthColumn() {
			this(0);
		}
		
		public OurFixedWidthColumn(int index) {
			this(index, 0);
		}
		
		public OurFixedWidthColumn(int index, int length) {
			this.index = index;
			this.length = length;
		}
		
		@Override
		public int getIndex() {
			return index;
		}
		
		@Override
		public int getLength() {
			return length;
		}
	}
	
	private class OurField implements Field {
		@Override
		public String getLabel() {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public void testNoValuesSet() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(new OurField());
		FixedWidthColumn result2 = test.columnIndexFor(new OurField());
		FixedWidthColumn result3 = test.columnIndexFor(new OurField());
	
		assertEquals(1, result1.getIndex());
		assertEquals(1, result1.getLength());
		
		assertEquals(2, result2.getIndex());
		assertEquals(1, result2.getLength());
		
		assertEquals(3, result3.getIndex());
		assertEquals(-1, result3.getLength());
	}
	
	public void testJustIndexes() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(1));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(10));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(15));
	
		assertEquals(1, result1.getIndex());
		assertEquals(9, result1.getLength());
		
		assertEquals(10, result2.getIndex());
		assertEquals(5, result2.getLength());
		
		assertEquals(15, result3.getIndex());
		assertEquals(-1, result3.getLength());
	}
	
	public void testIndexesAndLengths() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(10, 4));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(20, 2));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(30, 5));
	
		assertEquals(10, result1.getIndex());
		assertEquals(4, result1.getLength());
		
		assertEquals(20, result2.getIndex());
		assertEquals(2, result2.getLength());
		
		assertEquals(30, result3.getIndex());
		assertEquals(5, result3.getLength());
	}
	
	public void testFixedWidthTypesNoValues() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn());
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn());
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn());
	
		assertEquals(1, result1.getIndex());
		assertEquals(1, result1.getLength());
		
		assertEquals(2, result2.getIndex());
		assertEquals(1, result2.getLength());
		
		assertEquals(3, result3.getIndex());
		assertEquals(-1, result3.getLength());
	}
	
	public void testOverlappingValues() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(1, 20));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(10, 3));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(5, 1));
	
		assertEquals(1, result1.getIndex());
		assertEquals(20, result1.getLength());
		
		assertEquals(10, result2.getIndex());
		assertEquals(3, result2.getLength());
		
		assertEquals(5, result3.getIndex());
		assertEquals(1, result3.getLength());
	}
	
	
	public void testNoIndexJustLength() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(0, 1));
	
		assertEquals(1, result1.getIndex());
		assertEquals(1, result1.getLength());		
	}
	
	
	public void testResusingValuesWithLength() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(1, 9));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(10, 5));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(1, 2));
		FixedWidthColumn result4 = test.columnIndexFor(
				new OurFixedWidthColumn(3, 20));
	
		assertEquals(1, result1.getIndex());
		assertEquals(9, result1.getLength());
		
		assertEquals(10, result2.getIndex());
		assertEquals(5, result2.getLength());
		
		assertEquals(1, result3.getIndex());
		assertEquals(2, result3.getLength());
		
		assertEquals(3, result4.getIndex());
		assertEquals(20, result4.getLength());
	}
	
	public void testResusingValuesNoLength() {
		
		FixedWidthTextFieldHelper test = new FixedWidthTextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(1));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(10));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(1));
		FixedWidthColumn result4 = test.columnIndexFor(
				new OurFixedWidthColumn(3));
		FixedWidthColumn result5 = test.columnIndexFor(
				new OurFixedWidthColumn(24));
	
		assertEquals(1, result1.getIndex());
		assertEquals(9, result1.getLength());
		
		assertEquals(10, result2.getIndex());
		assertEquals(-1, result2.getLength());
		
		assertEquals(1, result3.getIndex());
		assertEquals(2, result3.getLength());
		
		assertEquals(3, result4.getIndex());
		assertEquals(21, result4.getLength());
		
		assertEquals(24, result5.getIndex());
		assertEquals(-1, result5.getLength());
	}
}

