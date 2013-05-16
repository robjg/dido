package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.ValueBinding;

public class FieldLayoutTest extends TestCase {

	public void testWriteSimple() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		assertEquals(1, test.getColumn());
		
		String[] headings = fields.headings();
		assertEquals(null, headings);
	}
	
	public void testWriteByName() throws DataException {
		
		SimpleFieldsOut fields = new SimpleFieldsOut();
		
		FieldLayout test = new FieldLayout();
		test.setName("Fruit");
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);

		DataWriter writer = test.writerFor(fields);
		
		assertEquals(false, writer.write("Apples"));

		String[] results = fields.values();
		
		assertEquals("Apples", results[0]);
		assertEquals(1, results.length);
		
		assertEquals(1, test.getColumn());
		
		String[] headings = fields.headings();
		assertEquals("Fruit", headings[0]);
		assertEquals(1, headings.length);
	}
	
	public void testReadSimple() throws DataException {
		
		MappedFieldsIn fields = new MappedFieldsIn();
		fields.setValues(new String[] { "Apple" });
		
		FieldLayout test = new FieldLayout();
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
	
	public void testReadByName() throws DataException {
		
		MappedFieldsIn fields = new MappedFieldsIn();
		fields.setHeadings(new String[] { "Stuff", "Fruit" });
		fields.setValues(new String[] { "Foo", "Apple" });
		
		FieldLayout test = new FieldLayout();
		test.setName("Fruit");
		
		ValueBinding binding = new ValueBinding();
		
		test.bind(binding);
		
		DataReader reader = test.readerFor(fields);
		
		Object result = reader.read();
		
		assertEquals("Apple", result);
		
		assertNull(reader.read());
	}
}
