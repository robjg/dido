package org.oddjob.dido.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.FixedWidthLayout;
import org.oddjob.dido.text.TextLayout;

public class LinesLayoutTest extends TestCase {

	String EOL = System.getProperty("line.separator");
	
	public void testReadThreeLines() throws DataException {
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
		
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn dataIn = new StreamLinesIn(input);
		
		LinesLayout test = new LinesLayout();
		test.setBinding(new DirectBinding());
		
		DataReader reader = test.readerFor(dataIn);
		
		Object object = reader.read();
		
		assertEquals("apples", object);

		object = reader.read();
		
		assertEquals("oranges", object);

		object = reader.read();
		
		assertEquals("bananas", object);

		object = reader.read();
		
		assertNull(object);
	}
	
	public void testReadLinesWithChildren() throws DataException {
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
		
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn dataIn = new StreamLinesIn(input);
				
		LinesLayout test = new LinesLayout();
		
		FixedWidthLayout fixed = new FixedWidthLayout();
		test.setOf(0, fixed);
		
		TextLayout text = new TextLayout();
		text.setBinding(new DirectBinding());
		fixed.setOf(0, text);
		
		DataReader reader = test.readerFor(dataIn);
		
		Object object = reader.read();

		assertEquals("apples", object);
		
		object = reader.read();
		
		assertEquals("oranges", object);
		
		object = reader.read();
		
		assertEquals("bananas", object);
		
		object = reader.read();
		
		assertEquals(null, object);
	}
	
	public void testEmptyInputSteam() throws DataException {
		
		InputStream input = new ByteArrayInputStream(new byte[0]);

		LinesIn dataIn = new StreamLinesIn(input);
				
		LinesLayout test = new LinesLayout();
		
		DataReader reader = test.readerFor(dataIn);
		
		assertNull(reader.read());

	}
	
	public void testWriteThreeLines() throws DataException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		StreamLinesOut dataOut = new StreamLinesOut(output);
		
		LinesLayout test = new LinesLayout();
		test.setBinding(new DirectBinding());
		
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("apples");
		
		writer.write("oranges");

		writer.write("bananas");
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
	
		assertEquals(lines, new String(output.toByteArray()));
	}

	public void testWriteLinesWithChildren() throws DataException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		StreamLinesOut dataOut = new StreamLinesOut(output);
		
		LinesLayout test = new LinesLayout();
		
		FixedWidthLayout fixed = new FixedWidthLayout();
		test.setOf(0, fixed);
		
		TextLayout text = new TextLayout();
		text.setBinding(new DirectBinding());
		
		fixed.setOf(0, text);
				
		DataWriter writer = test.writerFor(dataOut);

		writer.write("apples");
		
		writer.write("oranges");
		
		writer.write("bananas");
		
		String lines =
			"apples" + EOL +
			"oranges" + EOL +
			"bananas" + EOL;
	
		assertEquals(lines, new String(output.toByteArray()));
	}

}
