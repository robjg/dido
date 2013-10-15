package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReaderFactory;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.TextFieldsIn;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextLayout2;

public class ChildReaderTest extends TestCase {

	public void testReadNoChildren() throws DataException {

		Iterable<DataReaderFactory> children = 
				new ArrayList<DataReaderFactory>();
		
		TextIn dataIn = new StringTextIn("Apples");
		
		ChildReader test = new ChildReader(children, dataIn);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenNoBinding() throws DataException {

		TextLayout2 child1 = new TextLayout2();
		TextLayout2 child2 = new TextLayout2();
		TextLayout2 child3 = new TextLayout2();

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextFieldsIn dataIn = new TextFieldsIn();
		dataIn.setText("Apples");
		
		ChildReader test = new ChildReader(children, dataIn);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenWithOneBinding() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout2 child1 = new TextLayout2();
		TextLayout2 child2 = new TextLayout2();
		child2.bind(valueBinding);
		TextLayout2 child3 = new TextLayout2();

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextFieldsIn dataIn = new TextFieldsIn();
		dataIn.setText("Oranges");
		
		ChildReader test = new ChildReader(children, dataIn);

		Object result = test.read();

		assertEquals("r", result);
		
		assertEquals(null, test.read());
	}
	
	public void testReadChildrenSeveralBindings() throws DataException {

		DirectBinding valueBinding = new DirectBinding();
		
		TextLayout2 child1 = new TextLayout2();
		TextLayout2 child2 = new TextLayout2();
		child2.bind(valueBinding);
		TextLayout2 child3 = new TextLayout2();
		child3.bind(valueBinding);

		Iterable<? extends DataReaderFactory> children = 
				Arrays.asList(child1, child2, child3);
		
		TextFieldsIn dataIn = new TextFieldsIn();
		dataIn.setText("Oranges");
		
		ChildReader test = new ChildReader(children, dataIn);

		Object result = test.read();

		assertEquals("r", result);
		
		result = test.read();

		assertEquals("anges", result);
		
		assertEquals(null, test.read());
	}
}
