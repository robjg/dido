package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.field.Field;

public class TextFieldHelperTest extends TestCase {

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
		
		TextFieldHelper test = new TextFieldHelper();
		
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
		
		TextFieldHelper test = new TextFieldHelper();
		
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
		
		TextFieldHelper test = new TextFieldHelper();
		
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
		
		TextFieldHelper test = new TextFieldHelper();
		
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
		
		TextFieldHelper test = new TextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(1, 20));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(10, 3));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(5, 1));
	
		assertEquals(1, result1.getIndex());
		assertEquals(4, result1.getLength());
		
		assertEquals(10, result2.getIndex());
		assertEquals(3, result2.getLength());
		
		assertEquals(5, result3.getIndex());
		assertEquals(1, result3.getLength());
	}
	
	
	public void testInsertingHighestFirst() {
		
		TextFieldHelper test = new TextFieldHelper();
		
		FixedWidthColumn result1 = test.columnIndexFor(
				new OurFixedWidthColumn(30));
		FixedWidthColumn result2 = test.columnIndexFor(
				new OurFixedWidthColumn(20));
		FixedWidthColumn result3 = test.columnIndexFor(
				new OurFixedWidthColumn(10));
	
		assertEquals(30, result1.getIndex());
		assertEquals(-1, result1.getLength());
		
		assertEquals(20, result2.getIndex());
		assertEquals(10, result2.getLength());
		
		assertEquals(10, result3.getIndex());
		assertEquals(10, result3.getLength());
	}
}

