package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class HeadingsFieldsOutTest extends TestCase {

	public void testNoHeadingsNoHeadings() throws DataException {
		
		FieldsOut test = new SimpleFieldsOut();
		
		int nameCol = test.columnForHeading(null, 0);
		int ageCol = test.columnForHeading(null, 0);
		int cityCol = test.columnForHeading(null, 0);
		
		assertEquals(1, nameCol);
		assertEquals(2, ageCol);
		assertEquals(3, cityCol);
		
	}
	
	public void testHeadingsWithNoHeadings() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		assertEquals(1, test.columnForHeading("age", 0));
		assertEquals(2, test.columnForHeading("city", 0));
		assertEquals(4, test.columnForHeading("name", 4));
				
		String[] results = test.values();
		
		assertNull(results);
	}
	
	public void testHeadingsWithHeadings() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		assertEquals(1, test.columnForHeading("age", 0));
		assertEquals(2, test.columnForHeading("city", 0));
		assertEquals(4, test.columnForHeading("name", 4));
		
		String[] results = test.headings();
		
		assertEquals("age", results[0]);
		assertEquals("city", results[1]);
		assertEquals(null, results[2]);
		assertEquals("name", results[3]);
	}
}
