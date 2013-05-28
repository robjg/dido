package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;


public class TextTest extends TestCase {

	public void testFieldWhereNextInNoChildren() throws DataException {

		TextLayout test = new TextLayout();
		
		TextIn text = new StringTextIn("John");
				
		DataReader reader = test.readerFor(text);
		
		test.bind(new ValueBinding());

		String next = (String) reader.read();
		
		assertNotNull(next);
		
		assertEquals("John", next);
		assertEquals("John", test.getValue());
		
		next = (String) reader.read();
		
		assertNull(next);
		assertEquals("John", test.getValue());
	}	
		
	public void testTextInWithChildren() throws DataException {

		TextLayout test = new TextLayout();
		test.setRaw(true);
		
		TextLayout c1 = new TextLayout();
		c1.setFrom(0);
		c1.setLength(5);
		
		TextLayout c2 = new TextLayout();
		c2.setFrom(5);
		c2.setLength(10);
		c2.setRaw(true);

		test.setOf(0, c1);
		test.setOf(1, c2);
		
		TextIn text = new StringTextIn("Big  Cheese  ");
				
		DataReader reader = test.readerFor(text);
		
		String next = (String) reader.read();
		
		assertNull(next);
		
		
		assertEquals("Big", c1.getValue());
		assertEquals("Cheese  ", c2.getValue());
	}	
	
	public void testOutput() throws DataException {
		
		TextLayout test = new TextLayout();

		test.bind(new ValueBinding());
		
		StringTextOut dataOut = new StringTextOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		assertFalse(writer.write("apples"));
		
		assertEquals("apples", dataOut.toValue(String.class));
	}
	
	public void testTextOutWithChildren() throws DataException {

		TextLayout test = new TextLayout();
		
		TextLayout c1 = new TextLayout();
		c1.setFrom(0);
		c1.setLength(5);
		c1.setRaw(true);
		
		TextLayout c2 = new TextLayout();
		c2.setFrom(5);
		c2.setLength(10);

		test.setOf(0, c1);
		test.setOf(1, c2);
		
		c1.bind(new ValueBinding());
		c2.bind(new ValueBinding());
		
		StringTextOut dataOut = new StringTextOut();
	
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("Big");
				
		assertEquals("Big  Big       ", dataOut.toValue(String.class));
	}
}
