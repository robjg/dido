package org.oddjob.dido.beanbus;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataOutDestinationTest extends TestCase {

	private static final Logger logger = LoggerFactory.getLogger(
			DataOutDestinationTest.class);
	
	public static class Fruit {
		
		private String type;
		
		private int quantity;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
	}
	
	public void testExample() {
		
		File file = new File(getClass().getResource(
				"DataOutDestinationExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
			
			assertEquals(ParentState.COMPLETE, oddjob.lastStateEvent().getState());
		}
				
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals("type,quantity", lines[0].trim());
		assertEquals("Apple,5", lines[1].trim());
		assertEquals("Orange,2", lines[2].trim());
		assertEquals("Pear,7", lines[3].trim());
		
		assertEquals(4, lines.length);
		
		oddjob.destroy();
	}
}

