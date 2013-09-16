package org.oddjob.dido.text;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.DirectBinding;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.state.ParentState;

public class DelimitedLayoutTest extends TestCase {
	
	public void testVerySimpleNoChild() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();
		
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Apples,red", results.getLines().get(0));
		assertEquals("Bananas,yellow", results.getLines().get(1));
		assertEquals(2, results.getLines().size());
	}
	
	public void testWithHeadingsNoChild() throws DataException {
		
		ListLinesOut results = new ListLinesOut();
		
		DirectBinding binding = new DirectBinding();
		
		DelimitedLayout test = new DelimitedLayout();

		test.setWithHeadings(true);
		test.setHeadings(new String[] {"Fruit", "colour" });
		test.bind(binding);
		
		DataWriter writer = test.writerFor(results);
		
		writer.write(new String[] {"Apples", "red" });
		writer.write(new String[] {"Bananas", "yellow" });
		
		assertEquals("Fruit,colour", results.getLines().get(0));
		assertEquals("Apples,red", results.getLines().get(1));
		assertEquals("Bananas,yellow", results.getLines().get(2));
		assertEquals(3, results.getLines().size());
	}
	
	public void testSimpleReadWriteExample() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"DelimitedSimplestReadWrite.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("read.data.input", String[].class);
		
		String[] result = lookup.lookup("write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		assertEquals(expected[3], result[3]);
		
		assertEquals(expected.length, result.length);
	}
	
	public static class Fruit {
		
		private String fruit;
		private int quantity;
		private double price;
		
		public String getFruit() {
			return fruit;
		}
		public void setFruit(String type) {
			this.fruit = type;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
	}
	
	public void testReadWriteByTypeExample() throws ArooaPropertyException, ArooaConversionException {
		
		File file = new File(getClass().getResource(
				"DelimitedReadWriteByType.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String[] expected = lookup.lookup("read.data.input", String[].class);
		
		String[] result = lookup.lookup("write.data.output", String[].class);
		
		assertEquals(expected[0], result[0]);
		assertEquals(expected[1], result[1]);
		assertEquals(expected[2], result[2]);
		assertEquals(expected[3], result[3]);
		
		assertEquals(expected.length, result.length);
	}
}
