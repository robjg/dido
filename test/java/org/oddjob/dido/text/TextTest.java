package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;


public class TextTest extends TestCase {

	public void testFieldWhereNextInNoChildren() throws DataException {

		TextLayout test = new TextLayout();
		
		TextFieldsIn text = new TextFieldsIn();
		text.setText("John");
				
		DataReader reader = test.readerFor(text);
		
		test.bind(new DirectBinding());

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
		
		FixedWidthLayout fixedWidthLayout = new FixedWidthLayout();
		test.setOf(0, fixedWidthLayout);
		
		TextLayout c1 = new TextLayout();
		c1.setIndex(1);
		c1.setLength(5);
		
 		TextLayout c2 = new TextLayout();
		c2.setIndex(6);
		c2.setLength(10);
		c2.setRaw(true);
		
		fixedWidthLayout.setOf(0, c1);
		fixedWidthLayout.setOf(1, c2);

		TextFieldsIn text = new TextFieldsIn();
		text.setText("Big  Cheese  ");

		DataReader reader = test.readerFor(text);

		String next = (String) reader.read();

		assertNull(next);

		assertEquals("Big", c1.getValue());
		assertEquals("Cheese  ", c2.getValue());
	}	
	
	public void testOutput() throws DataException {
		
		TextLayout test = new TextLayout();

		test.bind(new DirectBinding());
		
		TextFieldsOut dataOut = new TextFieldsOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		assertFalse(writer.write("apples"));
		
		assertEquals("apples", dataOut.getText());
	}
	
	public void testTextOutWithChildren() throws DataException {

		TextLayout test = new TextLayout();
		
		FixedWidthLayout fixedWidthLayout = new FixedWidthLayout();
		test.setOf(0, fixedWidthLayout);
		
		TextLayout c1 = new TextLayout();
		c1.setIndex(1);
		c1.setLength(5);
		c1.setRaw(true);
		
		TextLayout c2 = new TextLayout();
		c2.setIndex(6);
		c2.setLength(10);

		fixedWidthLayout.setOf(0, c1);
		fixedWidthLayout.setOf(1, c2);
		
		c1.bind(new DirectBinding());
		c2.bind(new DirectBinding());
		
		TextFieldsOut dataOut = new TextFieldsOut();
	
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("Big");
				
		assertEquals("Big  Big       ", dataOut.getText());
	}
}
