package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;

public class FieldTest extends TestCase {

	public void testInputNoChildren() throws DataException {

		FieldLayout test = new FieldLayout();
		
		SimpleFieldsIn fields = new SimpleFieldsIn();
		
		fields.setHeadings(new String[] { "name" });
		fields.setValues(new String[] { "John" });

		test.bind(new DirectBinding());
		
		DataReader reader = test.readerFor(fields);
		
		String result = (String) reader.read();
				
		assertEquals("John", result);
		
		assertNull(reader.read());
		
		reader.close();
		
		fields.setValues(new String[] { "Jane" });
		
		reader = test.readerFor(fields);
				
		result = (String) reader.read();
		
		assertEquals("Jane", result);
		
		assertNull(reader.read());
		
		reader.close();
	}	
	
	public void testOutput() throws DataException {
		
		FieldLayout test = new FieldLayout();

		test.bind(new DirectBinding());
		
		SimpleFieldsOut dataOut = new SimpleFieldsOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("apples");
		
		assertEquals(0, test.getColumnIndex());
		
		assertEquals("apples", dataOut.values()[0]);		
		
		writer.write("oranges");
				
		assertEquals("oranges", dataOut.values()[0]);
	}
}
