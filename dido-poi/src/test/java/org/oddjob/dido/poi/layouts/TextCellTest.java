package org.oddjob.dido.poi.layouts;

import junit.framework.TestCase;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class TextCellTest extends TestCase {

	public void testWriteAndReadTextCellOfNamedValues() throws ArooaPropertyException, ArooaConversionException, IOException {
		
		File file = new File(getClass().getResource(
				"TextCellOfNamedValues.xml").getFile());

		Properties properties = new Properties();
		properties.setProperty("work.dir", OurDirs.workPathDir(TextCellTest.class).toString());

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		oddjob.setProperties(properties);
		
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
