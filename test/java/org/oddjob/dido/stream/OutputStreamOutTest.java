package org.oddjob.dido.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.oddjob.dido.DataException;
import org.oddjob.dido.stream.OutputStreamOut;

import junit.framework.TestCase;


public class OutputStreamOutTest extends TestCase {

	public String EOL = System.getProperty("line.separator");
	
	public void testWriteText() throws DataException, IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		OutputStreamOut test = new OutputStreamOut(out);
		
		test.write("Apple");
		test.newLine();
		
		test.flush();
	
		test.write("Orange");
		test.newLine();
		
		test.flush();
		
		out.close();
	
		String expected = 
			"Apple" + EOL +
			"Orange" + EOL;
		
		assertEquals(expected, new String(out.toByteArray()));		
	}
}
