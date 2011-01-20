package org.oddjob.dido.text;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;


public class TextTest extends TestCase {

	public void testFieldWhereNextInNoChildren() {

		Text test = new Text();
		
		TextIn text = new StringTextIn("John");
				
		WhereNextIn<TextIn> next = test.in(text);
		
		assertNotNull(next);
		
		assertNull(next.getChildren());
		assertNull(next.getChildData());
		
		assertEquals("John", test.getValue());
	}	
		
	public void testTextInWithChildren() throws DataException {

		Text test = new Text();
		test.setRaw(true);
		
		Text c1 = new Text();
		c1.setFrom(0);
		c1.setLength(5);
		
		Text c2 = new Text();
		c2.setFrom(5);
		c2.setLength(10);
		c2.setRaw(true);

		test.setIs(0, c1);
		test.setIs(1, c2);
		
		TextIn text = new StringTextIn("Big  Cheese  ");
				
		WhereNextIn<TextIn> next = test.in(text);
		
		assertNotNull(next);
		
		
		DataNode<TextIn, ?, ?, ?>[] children = next.getChildren();
		
		TextIn childData = next.getChildData();
		
		children[0].in(childData);
		children[1].in(childData);
		
		assertEquals("Big", c1.getValue());
		assertEquals("Cheese  ", c2.getValue());
	}	
	
	public void testOutput() throws DataException {
		
		Text test = new Text();
		
		test.setValue("apples");
		
		final AtomicReference<String> result = new AtomicReference<String>();
		
		StringTextOut dataOut = new StringTextOut() {
			
			@Override
			public boolean flush() throws DataException {
				result.set(this.toString());
				return true;
			}
		};
		
		WhereNextOut<TextOut> where = test.out(dataOut);
		
		assertNotNull(where);
		assertNull(where.getChildren());
		assertNull(where.getChildData());
		
		dataOut.flush();
		
		assertEquals("apples", result.get());
	}
	
	public void testTextOutWithChildren() throws DataException {

		Text test = new Text();
		
		Text c1 = new Text();
		c1.setFrom(0);
		c1.setLength(5);
		c1.setRaw(true);
		
		Text c2 = new Text();
		c2.setFrom(5);
		c2.setLength(10);

		test.setIs(0, c1);
		test.setIs(1, c2);
		
		c1.setValue("Big");
		c2.setValue("Cheese");
		
		final AtomicReference<String> result = new AtomicReference<String>();
		
		StringTextOut dataOut = new StringTextOut() {
			
			@Override
			public boolean flush() throws DataException {
				result.set(this.toString());
				return true;
			}
		};
				
		WhereNextOut<TextOut> where = test.out(dataOut);
		
		assertNotNull(where);
		
		DataNode<?, ?, TextOut, ?>[] children = where.getChildren();
		
		TextOut childData = where.getChildData();
		
		children[0].out(childData);
		children[1].out(childData);
		
		childData.flush();
		dataOut.flush();
		
		assertEquals("Big  Cheese    ", result.get());
	}
}
