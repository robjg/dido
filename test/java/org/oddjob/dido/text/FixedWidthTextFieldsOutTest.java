package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.tabular.ColumnOut;

public class FixedWidthTextFieldsOutTest extends TestCase {

	private class OurFixedWidthColumn
	implements FixedWidthColumn {
		
		private final int index;
		private final int length;
		
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
		
		@Override
		public String getLabel() {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public void testWithDefaultValueForIndexAndLength() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		assertFalse(test.isWrittenTo());
		
		assertEquals(null, test.getText());
		
		ColumnOut<String> out = test.outFor(new OurFixedWidthColumn(0, 0));
		
		assertEquals(1, out.getColumnIndex());
		
		out.setData("Apple");
		
		assertEquals("Apple", test.getText());
	}

	public void testTruncatedWhenTextGreaterThanLength() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out = test.outFor(new OurFixedWidthColumn(0, 6));
		
		out.setData("Apples and Oranges");
		
		assertEquals("Apples", test.getText());
	}

	public void testPaddedWhenLengthGreaterThanText() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out = test.outFor(new OurFixedWidthColumn(0, 12));
				
		out.setData("Apple");
		
		assertEquals("Apple       ", test.getText());
	}

	// Not sure what the desired behaviour should be!
	public void testOneFieldInTheMiddleOfAnother() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out1 = test.outFor(new OurFixedWidthColumn(0, 18));
		ColumnOut<String> out2 = test.outFor(new OurFixedWidthColumn(8, 3));
		
		out1.setData("Apples and Oranges");
		
		out2.setData("or");

		assertEquals("Apples or  Oranges", test.getText());
	}

	public void testOverrideEndOfAFieldWithAnother() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out1 = test.outFor(new OurFixedWidthColumn(0, 0));
		ColumnOut<String> out2 = test.outFor(new OurFixedWidthColumn(12, 0));
		
		out1.setData("Apples and Pears");
		
		out2.setData("Oranges");

		assertEquals("Apples and Oranges", test.getText());
	}

	public void testOverrideEndOfFieldAndTruncateData() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out1 = test.outFor(new OurFixedWidthColumn(0, 0));
		ColumnOut<String> out2 = test.outFor(new OurFixedWidthColumn(8, 3));
		
		out1.setData("Apples and Oranges");
		
		out2.setData("or maybe");

		assertEquals("Apples or ", test.getText());
	}
	
	public void testManySections() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out1 = test.outFor(new OurFixedWidthColumn(1, 10));
		ColumnOut<String> out2 = test.outFor(new OurFixedWidthColumn(11, 10));
		ColumnOut<String> out3 = test.outFor(new OurFixedWidthColumn(21, 10));
		
		out1.setData("Apples");
		out2.setData("Oranges");
		out3.setData("Pears");
		
		assertEquals("Apples    Oranges   Pears     ", test.getText());
	}
	
	public void testStartingPastStart() throws DataException {

		FixedWidthTextFieldsOut test = new FixedWidthTextFieldsOut();
		
		ColumnOut<String> out = test.outFor(new OurFixedWidthColumn(6, -1));
		
		out.setData("Apples");
		
		assertEquals("     Apples", test.getText());
	}

}
