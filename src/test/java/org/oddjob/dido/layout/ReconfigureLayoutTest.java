package org.oddjob.dido.layout;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.state.ParentState;

public class ReconfigureLayoutTest extends TestCase {

	public static class Snack {
		
		private String fruit;
		
		private int line;
		
		public String getFruit() {
			return fruit;
		}
		
		public void setFruit(String fruit) {
			this.fruit = fruit;
		}
		
		public int getLine() {
			return line;
		}
		public void setLine(int line) {
			this.line = line;
		}
	}
	
	static final String LS = System.getProperty("line.separator");
	
	public void testReadAndWrite() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass(
				).getResource("ReconfigureLayoutExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		assertEquals(4, (int) lookup.lookup("read/lines.lineCount", int.class));
		
		@SuppressWarnings("unchecked") 
		List<Snack> readResults = (List<Snack>) lookup.lookup(
				"read.beans", List.class);
		
		Snack snack = readResults.get(0);
		
		assertEquals(1, snack.line);
		assertEquals("apples", snack.fruit);
		
		snack = readResults.get(1);
		
		assertEquals("pears", snack.fruit);
		assertEquals(2, snack.line);
		
		snack = readResults.get(2);
		
		assertEquals(3, snack.line);
		assertEquals("bananas", snack.fruit);
		
		snack = readResults.get(3);
		
		assertEquals(4, snack.line);
		assertEquals("oranges", snack.fruit);
		
		assertEquals(4, readResults.size());
		
		String writeResults = lookup.lookup("vars.result", String.class);
		
		String expected = "apples" + LS +
				"pears" + LS +
				"bananas" + LS +
				"oranges" + LS;
		
		assertEquals(expected, writeResults);
	}
}
