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
		
		test.writeLine("Orange");
		
		out.close();
	
		String expected = 
			"Apple" + EOL +
			"Orange" + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
	
	
	public void testSimpleNested() throws DataException, IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamLinesOut test = new StreamLinesOut(out);

		LinesOut nested = test.provideDataOut(LinesOut.class);
		
		nested.writeLine("Apple");
		
		nested.writeLine("Orange");
		
		out.close();
	
		String expected = 
			"Apple" + EOL +
			"Orange" + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
	
	public void testWriteNested() throws DataException, IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamLinesOut test = new StreamLinesOut(out);

		LinesOut nested = test.provideDataOut(LinesOut.class);

		TextOut textOut = nested.provideDataOut(TextOut.class);

		assertFalse(nested.isWrittenTo());
		
		textOut.append("Apples");
		
		textOut = nested.provideDataOut(TextOut.class);
		
		textOut.append(" can be Green.");
		
		assertTrue(nested.isWrittenTo());
		
		String data = nested.lastLine();
				
		nested.writeLine(data);
		
		nested.writeLine("----------------");
		
		assertFalse(nested.isWrittenTo());
		
		assertEquals(false, test.isWrittenTo());
		
		textOut = nested.provideDataOut(TextOut.class);

		assertFalse(nested.isWrittenTo());
		
		textOut.append("Pears are normally Green.");
		
		assertTrue(nested.isWrittenTo());
		
		nested.writeLine(nested.lastLine());
		
		assertEquals(false, test.isWrittenTo());
		
		out.close();
	
		String expected = 
			"Apples can be Green." + EOL +
			"----------------" + EOL +
			"Pears are normally Green." + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
}
