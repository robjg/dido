package org.oddjob.dido.text;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class HeadingsFieldsOutTest extends TestCase {

	public void testNoHeadingsNoHeadings() throws DataException {
		
		FieldsOut test = new SimpleFieldsOut();
		
		int nameCol = test.writeHeading(null, 0);
		int ageCol = test.writeHeading(null, 0);
		int cityCol = test.writeHeading(null, 0);
		
		assertEquals(1, nameCol);
		assertEquals(2, ageCol);
		assertEquals(3, cityCol);
		
	}
	
	public void testHeadingsWithNoHeadings() throws DataException {
		
		FieldsOut test = new SimpleFieldsOut();
		
		assertEquals(1, test.writeHeading("age", 0));
		assertEquals(2, test.writeHeading("city", 0));
		assertEquals(4, test.writeHeading("name", 4));
				
		String[] results = test.toValue(String[].class);
		
		assertNull(results);
	}
	
	public void testHeadingsWithHeadings() throws DataException {
		
		SimpleFieldsOut test = new SimpleFieldsOut();
		
		assertEquals(1, test.writeHeading("age", 0));
		assertEquals(2, test.writeHeading("city", 0));
		assertEquals(4, test.writeHeading("name", 4));
		
		String[] results = test.headings();
		
		assertEquals("age", results[0]);
		assertEquals("city", results[1]);
		assertEquals(null, results[2]);
		assertEquals("name", results[3]);
	}
}
