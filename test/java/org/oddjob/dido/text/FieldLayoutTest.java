package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;

public class FieldLayoutTest extends TestCase {

	public void testWriteSimple() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals(0, headings.length);
	}
	
	public void testWriteByName() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		test.setName("fruit");
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		String[] headings = fields.headings();
		assertEquals("Fruit", headings[0]);
		assertEquals(1, headings.length);
	}
	
	public void testReadSimple() throws DataException {
		
		SimpleFieldsIn fields = new SimpleFieldsIn();
		fields.setValues(new String[] { "Apple" });
		
		FieldLayout test = new FieldLayout();
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
	
	public void testReadByName() throws DataException {
		
		SimpleFieldsIn fields = new SimpleFieldsIn();
		fields.setHeadings(new String[] { "Stuff", "Fruit" });
		fields.setValues(new String[] { "Foo", "Apple" });
		
		FieldLayout test = new FieldLayout();
		test.setLabel("Fruit");
		
		DirectBinding binding = new DirectBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
}
