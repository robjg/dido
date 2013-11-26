package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReaderFactory;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.FixedWidthTextFieldsIn;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextLayout;

public class ChildReaderTest extends TestCase {

	public void testReadNoChildren() throws DataException {

		Iterable<DataReaderFactory> children = 
				new ArrayList<DataReaderFactory>();
		
		TextIn dataIn = new StringTextIn("Apples");
		
		ChildReader test = new ChildReader(children, dataIn);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenNoBinding() throws DataException {

		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		FixedWidthTextFieldsIn dataIn = new FixedWidthTextFieldsIn();
		dataIn.setText("Apples");
		
		ChildReader test = new ChildReader(children, dataIn);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenWithOneBinding() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.setBinding(valueBinding);
		TextLayout child3 = new TextLayout();

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		FixedWidthTextFieldsIn dataIn = new FixedWidthTextFieldsIn();
		dataIn.setText("Oranges");
		
		ChildReader test = new ChildReader(children, dataIn);

		Object result = test.read();

		assertEquals("r", result);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenSeveralBindings() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout child1 = new TextLayout();
		TextLayout child2 = new TextLayout();
		child2.setBinding(valueBinding);
		TextLayout child3 = new TextLayout();
		child3.setBinding(valueBinding);

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		FixedWidthTextFieldsIn dataIn = new FixedWidthTextFieldsIn();
		dataIn.setText("Oranges");
		
		ChildReader test = new ChildReader(children, dataIn);

		Object result = test.read();

		assertEquals("r", result);
		
		result = test.read();

		assertEquals("anges", result);
		
		assertEquals(null, test.read());
	}
}
