package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;

public class TextLayoutTest extends TestCase {

	public void testWriteSimple() throws DataException {
		
		StringTextOut dataOut = new StringTextOut();
		
		TextLayout test = new TextLayout();
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));

		assertEquals("Apples", dataOut.toValue(String.class));
	}
	
	public void testWriteSubstring() throws DataException {
		
		StringTextOut dataOut = new StringTextOut();
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);
		test.setFrom(4);
		test.setLength(3);

		DataWriter writer = test.writerFor(dataOut);
		
		assertEquals(false, writer.write("Apples"));

		assertEquals("    App", dataOut.toValue(String.class));
	}
	
	public void testReadSimple() throws DataException {
		
		StringTextIn dataIn = new StringTextIn("Apples");
				
		TextLayout test = new TextLayout();
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("Apples", result);
		
		assertNull(reader.read());
	}
	
	public void testReadSubstring() throws DataException {
		
		StringTextIn dataIn = new StringTextIn("Apples");
		
		TextLayout test = new TextLayout();
		test.setName("Fruit");
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);
		test.setFrom(3);
		test.setLength(3);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertEquals("les", result);
		
		assertNull(reader.read());
	}
}
