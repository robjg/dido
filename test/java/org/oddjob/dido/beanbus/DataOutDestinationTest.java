package org.oddjob.dido.beanbus;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.ConsoleCapture;
import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;

public class DataOutDestinationTest extends TestCase {

	private static final Logger logger = Logger.getLogger(
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
		console.capture(Oddjob.CONSOLE);
				
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, oddjob.lastStateEvent().getState());
		
		console.close();
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
