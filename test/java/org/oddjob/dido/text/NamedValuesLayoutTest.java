package org.oddjob.dido.text;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.state.ParentState;

public class NamedValuesLayoutTest extends TestCase {

	public void testReadFromFields() throws DataException {
		
		SimpleFieldsIn dataIn = new SimpleFieldsIn();
		
		NamedValuesLayout test = new NamedValuesLayout();
		
		test.bind(new DirectBinding());
		
		DataReader reader = test.readerFor(dataIn);
		
		dataIn.setValues(new String[] {"fruit = apple", "colour=red"});
		
		@SuppressWarnings("unchecked")
		Map<String, String> result = (Map<String, String>) reader.read();
		
		assertEquals("apple", result.get("fruit"));
		assertEquals("red", result.get("colour"));
		
		assertEquals(null, reader.read());
		
		reader.close();
	}
	
	public void testWriteToFields() throws DataException {
		
		SimpleFieldsOut dataOut = new SimpleFieldsOut();

		NamedValuesLayout test = new NamedValuesLayout();
		
		test.bind(new DirectBinding());
		
		DataWriter writer = test.writerFor(dataOut);
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("fruit", "apple");
		map.put("colour", "red");
		
		assertEquals(false, writer.write(map));
		
		String[] result = dataOut.values();

		assertEquals("fruit=apple", result[0]);
		assertEquals("colour=red", result[1]);
		
		assertEquals(2, result.length);
	}
	
	public void testExample() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"NamedValuesLayout.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("vars.testData", String[].class);
		
		String[] result = lookup.lookup("vars.resultData", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		
		assertEquals(expected.length, result.length);
	}
	
	public void testExampleWithChildLayouts() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"NamedValuesWithChildren.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("vars.testData", String[].class);
		
		String[] result = lookup.lookup("vars.resultData", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		
		assertEquals(expected.length, result.length);
	}
}
