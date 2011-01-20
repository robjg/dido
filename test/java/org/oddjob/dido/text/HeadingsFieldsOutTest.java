package org.oddjob.dido.text;

import java.util.concurrent.atomic.AtomicReference;

import org.oddjob.dido.DataException;

import junit.framework.TestCase;

public class HeadingsFieldsOutTest extends TestCase {

	public void testNoHeadingsNoHeadings() throws DataException {
		
		final AtomicReference<String[]> ref = new AtomicReference<String[]>();
		
		FieldsOut test = new HeadingsFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						ref.set(values);
					}
				}, false);
		
		int nameCol = test.writeHeading(null, 0);
		int ageCol = test.writeHeading(null, 0);
		int cityCol = test.writeHeading(null, 0);
		
		assertEquals(1, nameCol);
		assertEquals(2, ageCol);
		assertEquals(3, cityCol);
		
		assertFalse(test.flush());
	}
	
	public void testHeadingsWithNoHeadings() throws DataException {
		
		final AtomicReference<String[]> ref = new AtomicReference<String[]>();
				
		FieldsOut test = new HeadingsFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						ref.set(values);
					}
				}, false);
		
		assertEquals(1, test.writeHeading("age", 0));
		assertEquals(2, test.writeHeading("city", 0));
		assertEquals(4, test.writeHeading("name", 4));
		
		assertFalse(test.flush());
		
		String[] results = ref.get();
		
		assertNull(results);
	}
	
	public void testHeadingsWithHeadings() throws DataException {
		
		final AtomicReference<String[]> ref = new AtomicReference<String[]>();
				
		FieldsOut test = new HeadingsFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						ref.set(values);
					}
				}, true);
		
		assertEquals(1, test.writeHeading("age", 0));
		assertEquals(2, test.writeHeading("city", 0));
		assertEquals(4, test.writeHeading("name", 4));
		
		assertTrue(test.flush());

		String[] results = ref.get();
		
		assertEquals("age", results[0]);
		assertEquals("city", results[1]);
		assertEquals(null, results[2]);
		assertEquals("name", results[3]);
	}
}
