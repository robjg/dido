package org.oddjob.dido.text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.ListLinesIn;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.dido.stream.StreamLinesIn;

public class FixedWidthLayoutTest extends TestCase {
	
	public void testVerySimpleWriteNoChild() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		FixedWidthLayout test = new FixedWidthLayout();
		
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write("Apples  red");
		writer.write("Bananas yellow");
		
		assertEquals("Apples  red", results.getLines().get(0));
		assertEquals("Bananas yellow", results.getLines().get(1));
		assertEquals(2, results.getLines().size());
	}
	
	public void testVerySimpleReadNoChild() throws DataException {
		
		ListLinesIn lines = new ListLinesIn(Arrays.asList(
				"Apples  red", "Bananas yellow"));
		
		DirectBinding binding = new DirectBinding();
		
		FixedWidthLayout test = new FixedWidthLayout();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(lines);

		String result = (String) reader.read();
		
		assertEquals("Apples  red", result);
		
		result = (String) reader.read();
		
		assertEquals("Bananas yellow", result);
		
		result = (String) reader.read();
		
		assertEquals(null, result);
	}
	
	String LS = System.getProperty("line.separator");
	
	public void testReadingAsChildOfLines() throws DataException {
		
		String lines =
				"apples" + LS +
				"oranges" + LS +
				"bananas" + LS;
			
		InputStream input = new ByteArrayInputStream(lines.getBytes());

		LinesIn dataIn = new StreamLinesIn(input);

		LinesLayout test = new LinesLayout();

		FixedWidthLayout fixed = new FixedWidthLayout();
		test.setOf(0, fixed);

		fixed.bind(new DirectBinding());
		
		DataReader reader = test.readerFor(dataIn);

		Object object = reader.read();

		assertEquals("apples", object);

		object = reader.read();

		assertEquals("oranges", object);

		object = reader.read();

		assertEquals("bananas", object);

		object = reader.read();

		assertEquals(null, object);
		
		reader.close();
	}
}
