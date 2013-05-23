package org.oddjob.dido.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;


public class OutputStreamOutTest extends TestCase {

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
}
