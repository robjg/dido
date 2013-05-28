package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;

public class FieldTest extends TestCase {

	public void testInputNoChildren() throws DataException {

		FieldLayout test = new FieldLayout();
		
		MappedFieldsIn fields = new MappedFieldsIn();
		
		fields.setHeadings(new String[] { "name" });
		fields.setValues(new String[] { "John" });

		test.bind(new ValueBinding());
		
		DataReader reader = test.readerFor(fields);
		
		String result = (String) reader.read();
				
		assertEquals("John", result);
	}	
	
	public void testOutput() throws DataException {
		
		FieldLayout test = new FieldLayout();

		test.bind(new ValueBinding());
		
		SimpleFieldsOut dataOut = new SimpleFieldsOut();
		
		DataWriter writer = test.writerFor(dataOut);
		
		writer.write("apples");
		
		assertEquals(1, test.getColumn());
		
		assertEquals("apples", dataOut.values()[0]);		
		
		writer.write("oranges");
				
		assertEquals("oranges", dataOut.values()[0]);
	}
}
