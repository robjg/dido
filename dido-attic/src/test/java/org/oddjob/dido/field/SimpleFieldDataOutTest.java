package org.oddjob.dido.field;

import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;

public class SimpleFieldDataOutTest extends TestCase {

	private class MyField implements Field {
		
		private final String label;

		public MyField(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		};
	}
	
	public void testWritingData() throws DataException {
		
		SimpleFieldDataOut test = new SimpleFieldDataOut();
		
		FieldOut<String> field1 = test.outFor(new MyField("fruit"));
		
		FieldOut<String> field2 = test.outFor(new MyField("colour"));
		
		FieldOut<String> field3 = test.outFor(new MyField("shape"));
		
		test.clear();
		
		field1.setData("apple");
		field2.setData("red");
		field3.setData(null);
		
		assertEquals(true, test.isWrittenTo());
		
		Map<String, String> values = test.getValues();
		
		assertEquals("apple", values.get("fruit"));
		assertEquals("red", values.get("colour"));
		assertEquals(null, values.get("shape"));

		assertEquals(2, values.size());
		
		assertEquals(String.class, field1.getType());
		
		test.clear();
		assertEquals(false, test.isWrittenTo());
	}
	
	public void testNullLabel() throws DataException {
		
		SimpleFieldDataOut test = new SimpleFieldDataOut();
		
		FieldOut<?> field1 = test.outFor(new MyField(null));

		field1.setData(null);
		
		assertEquals(false, test.isWrittenTo());
		
		Map<String, String> values = test.getValues();
		
		assertEquals(0, values.size());
		
		assertEquals(null, values.get(null));
	}
}
