package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.tabular.Column;
import org.oddjob.dido.tabular.ColumnIn;

public class StringTextInTest extends TestCase {

	public void testAsTextIn() throws UnsupportedDataInException {
		
		StringTextIn test = new StringTextIn("Apples");
		
		TextIn textIn = test.provideDataIn(TextIn.class);
		
		assertEquals("Apples", textIn.getText());
	}
	
	public void testAsLinesIn() throws DataException {
		
		StringTextIn test = new StringTextIn("Apples");
		
		LinesIn linesIn = test.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", linesIn.readLine());
		
		assertEquals(null, linesIn.readLine());
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
	
	public void testAsTextFieldsIn() throws DataException {
		
		StringTextIn test = new StringTextIn("Apples");
		
		TextFieldsIn fieldsIn = test.provideDataIn(TextFieldsIn.class);
		
		ColumnIn<String> columnIn = fieldsIn.inFor(new OurColumn());
		
		assertEquals(1, columnIn.getColumnIndex());
		assertEquals(String.class, columnIn.getType());
		
		assertEquals("Apples", columnIn.getData());
	}
}
