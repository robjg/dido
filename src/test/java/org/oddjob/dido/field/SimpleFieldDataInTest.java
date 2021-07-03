package org.oddjob.dido.field;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataException;

import junit.framework.TestCase;

public class SimpleFieldDataInTest extends TestCase {

	private class MyField implements Field {
		
		private final String label;

		public MyField(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		};
	}
	
	public void testReadingData() throws DataException {
		
		SimpleFieldDataIn test = new SimpleFieldDataIn();
		
		FieldIn<?> field1 = test.inFor(new MyField("fruit"));
		
		FieldIn<?> field2 = test.inFor(new MyField("colour"));
		
		FieldIn<?> field3 = test.inFor(new MyField("shape"));
		
		Map<String, String> values = new HashMap<String, String>();
		
		values.put("fruit", "apple");
		values.put("colour", "red");
		
		test.setValues(values);		
		
		assertEquals("apple", field1.getData());
		assertEquals("red", field2.getData());
		assertEquals(null, field3.getData());
		
		assertEquals(String.class, field1.getType());
	}
	
	public void testNullLabel() throws DataException {
		
		SimpleFieldDataIn test = new SimpleFieldDataIn();
		
		FieldIn<?> field1 = test.inFor(new MyField(null));
		
		Map<String, String> values = new HashMap<String, String>();
		
		values.put("fruit", "apple");
		values.put("colour", "red");
		
		test.setValues(values);		
		
		assertEquals(null, field1.getData());
	}
}
