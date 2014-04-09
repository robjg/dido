package org.oddjob.dido.poi.layouts;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.ParentState;

public class TextCellTest extends TestCase {

	public void testWriteAndReadTextCellOfNamedValues() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"TextCellOfNamedValues.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("text-read.data.input", String[].class);
		
		String[] result = lookup.lookup("text-write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		
		assertEquals(expected.length, result.length);
		
		oddjob.destroy();
	}

}
