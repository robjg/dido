package org.oddjob.dido.text;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;

public class FieldTest extends TestCase {

	public void testInputNoChildren() {

		Field test = new Field();
		
		MappedFieldsIn fields = new MappedFieldsIn();
		
		fields.setHeadings(new String[] { "name" });
		fields.setValues(new String[] { "John" });
		
		test.begin(fields);
		
		WhereNextIn<TextIn> next = test.in(fields);
		
		assertNotNull(next);
		
		assertNull(next.getChildren());
		assertNull(next.getChildData());
		
		assertEquals("John", test.getValue());
	}	
	
	public void testOutput() throws DataException {
		
		final AtomicReference<String[]>	result = 
			new AtomicReference<String[]>();
			
		FieldsOut headingsOut = new HeadingsFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						result.set(values);
					}
				}, false);
			
		Field test = new Field();
		
		test.setValue("apples");
		
		test.begin(headingsOut);
		
		assertEquals(1, test.getColumn());
		
		MappedFieldsOut dataOut = new MappedFieldsOut(
				new MappedFieldsOut.FieldsWriter() {
					@Override
					public void write(String[] values) {
						result.set(values);
					}
				});
		
		WhereNextOut<TextOut> where = test.out(dataOut);
		
		assertNotNull(where);
		assertNull(where.getChildren());
		assertNull(where.getChildData());
		
		dataOut.flush();
		
		assertEquals("apples", result.get()[0]);		
		
		test.setValue("oranges");
		
		test.out(dataOut);
		
		dataOut.flush();
		
		assertEquals("oranges", result.get()[0]);
	}
}
