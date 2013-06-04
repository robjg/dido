package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriterFactory;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.bio.ValueBinding;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.TextLayout;
import org.oddjob.dido.text.TextOut;

public class ChildWriterTest extends TestCase {
	
	class OurValueNode implements ValueNode<String> {
		
		String value;
		
		@Override
		public Class<String> getType() {
			return String.class;
		}
		
		@Override
		public String value() {
			return value;
		}
		
		@Override
		public void value(String value) {
			this.value = value;
		}
	}
	
	public void testWriteNoChildren() throws DataException {

		Iterable<DataWriterFactory> children = 
				new ArrayList<DataWriterFactory>();
		
		TextOut dataIn = new StringTextOut();
		
		OurValueNode valueNode = new OurValueNode();
		
		ChildWriter test = new ChildWriter(children, valueNode, dataIn);
		
		assertFalse(null, test.write("Apples"));
	}
	
	public void testWriteChildrenNoBinding() throws DataException {

		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextOut dataOut = new StringTextOut();
		
		OurValueNode valueNode = new OurValueNode();
		
		ChildWriter test = new ChildWriter(children, valueNode, dataOut);
		
		assertEquals(false, test.write("Apples"));
		
		assertEquals("", dataOut.toValue(String.class));
	}
	
	public void testWriteChildrenWithOneBinding() throws DataException {

		ValueBinding valueBinding = new ValueBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.bind(valueBinding);
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextOut dataOut = new StringTextOut();
		
		OurValueNode valueNode = new OurValueNode();
		
		ChildWriter test = new ChildWriter(children, valueNode, dataOut);

		assertEquals(false, test.write("Apples"));

		assertEquals("Apples", dataOut.toValue(String.class));
	}
	
	public void testWriteChildrenSeveralBindings() throws DataException {

		ValueBinding valueBinding = new ValueBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.bind(valueBinding);
		TextLayout child3 = new TextLayout();
		child3.bind(valueBinding);

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextOut dataOut = new StringTextOut();
		
		OurValueNode valueNode = new OurValueNode();
		
		ChildWriter test = new ChildWriter(children, valueNode, dataOut);

		assertEquals(false, test.write("Apples"));

		assertEquals("Apples", dataOut.toValue(String.class));
		
		assertEquals(false, test.write("Apples"));
		
		assertEquals("Apples", dataOut.toValue(String.class));
		
	}
}
