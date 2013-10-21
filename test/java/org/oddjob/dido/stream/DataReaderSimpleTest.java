package org.oddjob.dido.stream;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.bio.Binding;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.IOStreamData;
import org.oddjob.dido.stream.LinesLayout;

public class DataReaderSimpleTest extends TestCase {

	public void testSimpleLineReading() throws DataException {
		
		String lines = "Apples\n" +
				"Oranges\n";
		
		IOStreamData ioData = new IOStreamData();
		ioData.setArooaSession(new StandardArooaSession());
		ioData.setInput(new ArooaObject(new ByteArrayInputStream(
				lines.getBytes())));
		
		LinesLayout node = new LinesLayout();
		
		Binding binding = new DirectBinding();
		
		node.bind(binding);
		
		DataReader reader = node.readerFor(ioData);
		
		Object result;

		result = reader.read();

		assertEquals("Apples", result);
		
		result = reader.read();

		assertEquals("Oranges", result);
		
		result = reader.read();

		assertEquals(null, result);
	}
}
