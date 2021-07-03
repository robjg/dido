package org.oddjob.dido.stream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.text.TextIn;

public class StreamLinesInTest extends TestCase {

	static String EOL = System.getProperty("line.separator");

	public void testSimpleReadIn() throws DataException {
		
		String lines =
				"apples" + EOL +
				"oranges" + EOL +
				"bananas" + EOL;
			
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn test = new StreamLinesIn(input);
		
		
		assertEquals("apples", test.readLine());
		assertEquals("oranges", test.readLine());
		assertEquals("bananas", test.readLine());
		assertNull(test.readLine());
		
	}
	
	public void testReadTextIn() throws DataException {
		
		String lines = "Apples" + EOL +
				"Oranges";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		assertEquals("Apples", test.readLine());

		TextIn textIn = test.provideDataIn(TextIn.class);
		
		assertEquals("Apples", textIn.getText());
		
		assertEquals("Oranges", test.readLine());

		textIn = test.provideDataIn(TextIn.class);
		
		assertEquals("Oranges", textIn.getText());
		
		assertEquals(null, test.readLine());

		try {
			test.provideDataIn(TextIn.class);
		}
		catch (DataException e) {
			assertEquals("No more lines.", e.getMessage());
		}
		
	}
	
	public void testReadLinesNestedParentDoesntRead() throws DataException {
		
		String lines = "Apples" + EOL +
				"Oranges" + EOL +
				"Bananas" + EOL + 
				"Pears";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		LinesIn nested = test.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", nested.readLine());
		assertEquals("Oranges", nested.readLine());
		assertEquals("Bananas", nested.readLine());
		assertEquals("Pears", nested.readLine());
		assertNull(nested.readLine());
		
	}
	
	public void testReadLinesNestedParentReadsFirst() throws DataException {
	
		String lines = "Apples" + EOL +
				"Oranges" + EOL +
				"Bananas" + EOL + 
				"Pears";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		assertEquals("Apples", test.readLine());
		
		LinesIn nested = test.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", nested.readLine());
		
		assertEquals("Oranges", nested.readLine());
		
		assertEquals("Bananas", test.readLine());
		
		nested = test.provideDataIn(LinesIn.class);
		
		assertEquals("Bananas", nested.readLine());
		
		assertEquals("Pears", nested.readLine());
		
		assertEquals(null, nested.readLine());
		
		assertEquals(null, test.readLine());
	}
	
	public void testReadLinesTwiceNestedFirstNestedReadsFirst() throws DataException {
		
		String lines = "Apples" + EOL +
				"Oranges" + EOL +
				"Bananas" + EOL + 
				"Pears";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		LinesIn nested = test.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", nested.readLine());
		
		LinesIn nested2 = nested.provideDataIn(LinesIn.class);
		
		assertEquals("Apples", nested2.readLine());
		
		assertEquals("Oranges", nested2.readLine());
		
		assertEquals("Bananas", nested.readLine());
		
		nested2 = nested.provideDataIn(LinesIn.class);
		
		assertEquals("Bananas", nested2.readLine());
		
		assertEquals("Pears", nested2.readLine());
		
		assertEquals(null, nested.readLine());
		
		assertEquals(null, test.readLine());
	}
}
