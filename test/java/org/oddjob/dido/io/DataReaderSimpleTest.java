package org.oddjob.dido.io;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.bio.DataBindingIn;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.stream.InputStreamIn;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.StreamIn;

public class DataReaderSimpleTest extends TestCase {

	public void testSimpleLineReading() throws DataException {
		
		String lines = "Apples\n" +
				"Oranges\n";
		
		StreamIn in = new InputStreamIn(new ByteArrayInputStream(
				lines.getBytes()));
		
		LinesLayout node = new LinesLayout();
		
		DataBindingIn bin = new ValueBinding();
		
		node.bind(bin);
		
		DataReader reader = node.readerFor(in);
		
		Object result;

		result = reader.read();

		assertEquals("Apples", result);
		
		result = reader.read();

		assertEquals("Oranges", result);
		
		result = reader.read();

		assertEquals(null, result);
	}
}
