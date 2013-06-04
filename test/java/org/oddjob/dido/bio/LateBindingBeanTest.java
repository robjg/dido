package org.oddjob.dido.bio;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.MockDataOut;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.bio.BeanBindingBeanTest.Basket;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.text.DelimitedLayout;
import org.oddjob.dido.text.FieldLayout;

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
	
	private class OurLinesOut extends MockDataOut
	implements LinesOut {
		
		private List<String> results = new ArrayList<String>();
		
		@Override
		public <T extends DataOut> T provide(Class<T> type)
				throws UnsupportedeDataOutException {
			if (type.isAssignableFrom(LinesOut.class)) {
				return type.cast(this);
			}
			else {
				throw new UnsupportedeDataOutException(getClass(), type);
			}
		}
		
		@Override
		public void writeLine(String text) throws DataException {
			results.add(text);
		}
	}
		
	public void testBindOutWithChilden() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		
		FieldLayout typeNode = new FieldLayout();
		typeNode.setName("type");
		
		DelimitedLayout root = new DelimitedLayout();
		root.setName("fruit");
		root.setOf(0, typeNode);
		
		root.bind(test);
		
		Fruit fruit = new Fruit();
		fruit.setType("apple");
		
		OurLinesOut dataOut = new OurLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertTrue(writer.write(fruit));

		assertEquals("apple", dataOut.results.get(0));
		assertEquals(1, dataOut.results.size());
		
		fruit.setType("pear");

		assertTrue(writer.write(fruit));
		
		assertEquals("pear", dataOut.results.get(1));
		assertEquals(2, dataOut.results.size());
		
		test.free();
	}

	public void testBindOutWithTypeConversion() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		
		FieldLayout costNode = new FieldLayout();
		costNode.setName("cost");
		
		DelimitedLayout root = new DelimitedLayout();
		root.setOf(0, costNode);
		
		root.bind(test);
		
		Basket basket = new Basket();
		basket.setCost(12.47);
		
		OurLinesOut dataOut = new OurLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertTrue(writer.write(basket));
		
		assertEquals("12.47", dataOut.results.get(0));
		
		assertEquals("12.47", costNode.value());
		
		test.free();
	}
		
}
