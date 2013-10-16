package org.oddjob.dido.bio;

import junit.framework.TestCase;

import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.bio.BeanBindingBeanTest.Basket;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.dido.text.DelimitedLayout;
import org.oddjob.dido.text.TextLayout;

public class LateBindingBeanTest extends TestCase {

	public static class Fruit {
		
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	public void testBindOutWithChilden() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		
		TextLayout typeNode = new TextLayout();
		typeNode.setName("type");
		
		DelimitedLayout root = new DelimitedLayout();
		root.setName("fruit");
		root.setOf(0, typeNode);
		
		root.bind(test);
		
		Fruit fruit = new Fruit();
		fruit.setType("apple");
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertTrue(writer.write(fruit));

		assertEquals("apple", dataOut.getLines().get(0));
		assertEquals(1, dataOut.getLines().size());
		
		fruit.setType("pear");

		assertTrue(writer.write(fruit));
		
		assertEquals("pear", dataOut.getLines().get(1));
		assertEquals(2, dataOut.getLines().size());
		
		test.free();
	}

	public void testBindOutWithTypeConversion() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		
		TextLayout costNode = new TextLayout();
		costNode.setName("cost");
		
		DelimitedLayout root = new DelimitedLayout();
		root.setOf(0, costNode);
		
		root.bind(test);
		
		Basket basket = new Basket();
		basket.setCost(12.47);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertTrue(writer.write(basket));
		
		assertEquals("12.47", dataOut.getLines().get(0));
		
		assertEquals("12.47", costNode.value());
		
		test.free();
	}
		
}
