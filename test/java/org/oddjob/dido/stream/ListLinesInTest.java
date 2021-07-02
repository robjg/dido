package org.oddjob.dido.stream;

import java.util.Arrays;

import org.oddjob.dido.DataException;
import org.oddjob.dido.text.TextIn;

import junit.framework.TestCase;

public class ListLinesInTest extends TestCase {

	public void testRead() throws DataException {
		
		LinesIn dataIn = new ListLinesIn(
				Arrays.asList(
						"type,quantity", 
						"apple,27", 
						"pear,42"));
		
		assertEquals("type,quantity", dataIn.readLine());
		assertEquals("apple,27", dataIn.readLine());
		
		TextIn text = dataIn.provideDataIn(TextIn.class);
		assertEquals("apple,27", text.getText());
		
		assertEquals("pear,42", dataIn.readLine());
		
		text = dataIn.provideDataIn(TextIn.class);
		assertEquals("pear,42", text.getText());
		
		assertEquals(null, dataIn.readLine());

	}
	
}
