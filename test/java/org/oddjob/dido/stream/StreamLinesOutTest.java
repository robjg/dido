package org.oddjob.dido.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.text.TextOut;


public class StreamLinesOutTest extends TestCase {

	public String EOL = System.getProperty("line.separator");
	
	public void testWriteText() throws DataException, IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamLinesOut test = new StreamLinesOut(out);
		
		test.writeLine("Apple");
		
		test.flush();
	
		test.writeLine("Orange");
		
		test.flush();
		
		out.close();
	
		String expected = 
			"Apple" + EOL +
			"Orange" + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
	
	public void testWriteNested() throws DataException, IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamLinesOut test = new StreamLinesOut(out);

		LinesOut nested = test.provide(LinesOut.class);

		TextOut textOut = nested.provide(TextOut.class);

		assertFalse(nested.hasData());
		
		textOut.append("Apples");
		
		textOut = nested.provide(TextOut.class);
		
		textOut.append(" can be Green.");
		
		assertTrue(nested.hasData());
		
		String data = nested.toValue(String.class);
				
		nested.writeLine(data);
		
		nested.writeLine("----------------");
		
		assertFalse(nested.hasData());
		
		assertTrue(test.hasData());
		
		data = test.toValue(String.class);
		
		assertTrue(test.hasData());
		
		test.writeLine(data);
		
		assertFalse(test.hasData());
		
		textOut = nested.provide(TextOut.class);

		assertFalse(nested.hasData());
		
		textOut.append("Pears are normally Green.");
		
		assertTrue(nested.hasData());
		
		nested.writeLine(nested.toValue(String.class));
		
		assertTrue(test.hasData());
		
		test.writeLine(test.toValue(String.class));
		
		assertFalse(test.hasData());
		
		out.close();
	
		String expected = 
			"Apples can be Green." + EOL +
			"----------------" + EOL +
			"Pears are normally Green." + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
}
