package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class MappedFieldsOutTest extends TestCase {

	public void testNextOut() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		assertEquals(false, test.isWrittenTo());
		
		test.setColumnData(1, "John");
		test.setColumnData(2, "34");
		test.setColumnData(3, "London");
		
		assertEquals(true, test.isWrittenTo());
		
		String[] results = test.values();
		
		assertEquals("John", results[0]);
		assertEquals("34", results[1]);
		assertEquals("London", results[2]);
	}
	
	public void testRandomColumn() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		test.setColumnData(5, "John");
		test.setColumnData(3, "34");
		test.setColumnData(6, "London");
		
		String[] results = test.values();
		
		assertEquals(null, results[0]);
		assertEquals(null, results[1]);
		assertEquals("34", results[2]);
		assertEquals(null, results[3]);
		assertEquals("John", results[4]);
		assertEquals("London", results[5]);
	}
	
	public void testNamedFields() throws DataException {
				
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		test.setColumnData(4, "John");
		test.setColumnData(1, "34");
		test.setColumnData(2, "London");
		
		String[] results = test.values();
		
		assertEquals("34", results[0]);
		assertEquals("London", results[1]);
		assertEquals(null, results[2]);
		assertEquals("John", results[3]);
	}
}
