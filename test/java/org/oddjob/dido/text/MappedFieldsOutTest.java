package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class MappedFieldsOutTest extends TestCase {

	public void testNextOut() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		test.setColumn(1, "John");
		test.setColumn(2, "34");
		test.setColumn(3, "London");
		
		String[] results = test.toValue(String[].class);
		
		assertEquals("John", results[0]);
		assertEquals("34", results[1]);
		assertEquals("London", results[2]);
	}
	
	public void testRandomColumn() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		test.setColumn(5, "John");
		test.setColumn(3, "34");
		test.setColumn(6, "London");
		
		String[] results = test.toValue(String[].class);
		
		assertEquals(null, results[0]);
		assertEquals(null, results[1]);
		assertEquals("34", results[2]);
		assertEquals(null, results[3]);
		assertEquals("John", results[4]);
		assertEquals("London", results[5]);
	}
	
	public void testNamedFields() throws DataException {
				
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		test.setColumn(4, "John");
		test.setColumn(1, "34");
		test.setColumn(2, "London");
		
		String[] results = test.toValue(String[].class);
		
		assertEquals("34", results[0]);
		assertEquals("London", results[1]);
		assertEquals(null, results[2]);
		assertEquals("John", results[3]);
	}
}
