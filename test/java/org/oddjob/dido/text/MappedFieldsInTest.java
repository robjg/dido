package org.oddjob.dido.text;

import org.oddjob.dido.text.MappedFieldsIn;

import junit.framework.TestCase;

public class MappedFieldsInTest extends TestCase {

	public void testWithHeadings() {
				
		String[] headings = { "name", "age", "city" };
		String[] values = { "John", "32", "London" };
		
		MappedFieldsIn test = new MappedFieldsIn();
		
		test.setHeadings(headings);
		test.setValues(values);
		
		assertEquals(1, test.columnFor("name", false, 0));
		assertEquals(2, test.columnFor("age", false, 0));
		assertEquals(3, test.columnFor("city", false, 0));
		assertEquals(0, test.columnFor("occupation", true, 0));
		
		assertEquals("John", test.getColumn(1));
		assertEquals("32", test.getColumn(2));
		assertEquals("London", test.getColumn(3));
		assertNull(test.getColumn(4));
	}
	
		
	public void testNoHeadings() {
		
		MappedFieldsIn test = new MappedFieldsIn();
		
		assertEquals(1, test.columnFor(null, false, 0));
		assertEquals(2, test.columnFor(null, false, 0));
		assertEquals(3, test.columnFor(null, false, 0));
		assertEquals(4, test.columnFor(null, false, 0));
		
		String[] values = { "John", "32", "London" };
		test.setValues(values);
		
		assertEquals("John", test.getColumn(1));
		assertEquals("32", test.getColumn(2));
		assertEquals("London", test.getColumn(3));
		assertNull(test.getColumn(4));
	}
	
	public void testOptionalNoHeadings() {
		
		MappedFieldsIn test = new MappedFieldsIn();
		
		assertEquals(1, test.columnFor("name", true, 0));
		assertEquals(2, test.columnFor("age", true, 0));
		assertEquals(3, test.columnFor("city", true, 0));
		assertEquals(4, test.columnFor("occupation", true, 0));
		
	}
	
	public void testNotOptionalNoHeadings() {
		
		String[] values = { "John", "32", "London" };
		
		MappedFieldsIn test = new MappedFieldsIn();
		
		assertEquals(1, test.columnFor("name", false, 0));
		assertEquals(2, test.columnFor("age", false, 0));
		assertEquals(3, test.columnFor("city", false, 0));
		assertEquals(4, test.columnFor("occupation", false, 0));
		
		test.setValues(values);
		
		assertEquals("John", test.getColumn(1));
		assertEquals("32", test.getColumn(2));
		assertEquals("London", test.getColumn(3));
		
		assertNull(test.getColumn(4));
	}
}
