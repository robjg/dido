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

		TextIn textIn = test.provide(TextIn.class);
		
		assertEquals("Apples", textIn.getText());
		
		assertEquals("Oranges", test.readLine());

		textIn = test.provide(TextIn.class);
		
		assertEquals("Oranges", textIn.getText());
		
		assertEquals(null, test.readLine());

		try {
			test.provide(TextIn.class);
		}
		catch (DataException e) {
			assertEquals("No more lines.", e.getMessage());
		}
		
	}
	
	public void testSimpleReadLinesNested() throws DataException {
		
		String lines = "Apples" + EOL +
				"Oranges" + EOL +
				"Bananas" + EOL + 
				"Pears";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		LinesIn nested = test.provide(LinesIn.class);
		
		assertEquals("Apples", nested.readLine());
		assertEquals("Oranges", nested.readLine());
		assertEquals("Bananas", nested.readLine());
		assertEquals("Pears", nested.readLine());
		assertNull(nested.readLine());
		
	}
	
	public void testReadLinesNested() throws DataException {
	
		String lines = "Apples" + EOL +
				"Oranges" + EOL +
				"Bananas" + EOL + 
				"Pears";
		
		StreamLinesIn test = new StreamLinesIn(
				new ByteArrayInputStream(lines.getBytes()));
		
		assertEquals("Apples", test.readLine());
		
		LinesIn nested = test.provide(LinesIn.class);
		
		assertEquals("Apples", nested.readLine());
		
		assertEquals("Oranges", nested.readLine());
		
		assertEquals("Bananas", test.readLine());
		
		nested = test.provide(LinesIn.class);
		
		assertEquals("Bananas", nested.readLine());
		
		assertEquals("Pears", nested.readLine());
		
		assertEquals(null, nested.readLine());
		
		assertEquals(null, test.readLine());
	}
}
