package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnOut;

public class StringTextOutTest extends TestCase {
	
	public void testAsTextOut() {
		
		StringTextOut test = new StringTextOut();
		
		test.append("Apples");
		
		assertEquals("Apples", test.toText());
	}
	
	public void testAsLines() throws DataException {
		
		StringTextOut test = new StringTextOut();
		
		LinesOut linesOut = test.provideDataOut(LinesOut.class);
		
		linesOut.writeLine("Apples");
		linesOut.writeLine("Pears");
		
		assertEquals("ApplesPears", test.toText());
	}
	
	private class OurColumn implements Column {
		
		@Override
		public int getIndex() {
			throw new RuntimeException("Unexpected.");
		}
		
		@Override
		public String getLabel() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
	public void testAsField() throws DataException {
		
		StringTextOut test = new StringTextOut();
		
		TextFieldsOut textFieldsOut = test.provideDataOut(TextFieldsOut.class);
		
		ColumnOut<String> columnOut = textFieldsOut.outFor(new OurColumn());
		
		assertEquals(1, columnOut.getColumnIndex());
		assertEquals(String.class, columnOut.getType());
		
		columnOut.setData("Apples");
		
		assertEquals("Apples", test.toText());
		
	}
}
