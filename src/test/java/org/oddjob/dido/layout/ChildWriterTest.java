package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriterFactory;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.StringTextOut;
import org.oddjob.dido.text.FixedWidthTextFieldsOut;
import org.oddjob.dido.text.TextLayout;
import org.oddjob.dido.text.TextOut;

public class ChildWriterTest extends TestCase {
	
	public void testWriteNoChildren() throws DataException {

		Iterable<DataWriterFactory> children = 
				new ArrayList<DataWriterFactory>();
		
		TextOut dataIn = new StringTextOut();
		
		ChildWriter test = new ChildWriter(children, dataIn);
		
		assertFalse(null, test.write("Apples"));
	}
	
	public void testWriteChildrenNoBinding() throws DataException {

		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
				
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
		
		ChildWriter test = new ChildWriter(children, dataOut);
		
		assertEquals(false, test.write("Apples"));
		
		assertEquals(null, dataOut.getText());
	}
	
	public void testWriteChildrenWithOneBinding() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.setIndex(1);
		child2.setLength(10);
		child2.setBinding(valueBinding);
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
				
		ChildWriter test = new ChildWriter(children, dataOut);

		assertEquals(false, test.write("Apples"));

		assertEquals("Apples    ", dataOut.getText());
	}
	
	public void testWriteChildrenSeveralBindings() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.setIndex(1);
		child2.setLength(10);
		child2.setBinding(valueBinding);
		TextLayout child3 = new TextLayout();
		child3.setIndex(11);
		child3.setLength(10);
		child3.setBinding(valueBinding);

		Iterable<? extends DataWriterFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		FixedWidthTextFieldsOut dataOut = new FixedWidthTextFieldsOut();
		
		ChildWriter test = new ChildWriter(children, dataOut);

		assertEquals(false, test.write("Apples"));

		assertEquals("Apples    Apples    ", dataOut.getText());

		// Writing again has no affect. The ChildWriter has no more
		// children to write to.
		assertEquals(false, test.write("Oranges"));
		
		assertEquals("Apples    Apples    ", dataOut.getText());
		
		test.close();
		
		test = new ChildWriter(children, dataOut);

		assertEquals(false, test.write("Oranges"));
		
		assertEquals("Oranges   Oranges   ", dataOut.getText());		
	}
}
