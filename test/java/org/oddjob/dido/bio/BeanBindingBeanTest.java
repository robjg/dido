package org.oddjob.dido.bio;

import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.stream.ListLinesOut;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.text.DelimitedLayout;
import org.oddjob.dido.text.FieldLayout;
import org.oddjob.dido.text.StringTextIn;
import org.oddjob.dido.text.TextIn;
import org.oddjob.dido.text.TextLayout;

public class BeanBindingBeanTest extends TestCase {

	public static class Fruit {
		
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	
	private class OurLinesIn 
	implements LinesIn {

		final String[] lines;
		
		int i = -1;
		
		public OurLinesIn(String[] lines) {
			this.lines = lines;
		}
		
		@Override
		public <T extends DataIn> T provide(Class<T> type)
				throws UnsupportedeDataInException {
			
			if (type.isAssignableFrom(LinesIn.class)) {
				return type.cast(this);
			}
			
			if (type.isAssignableFrom(TextIn.class)) {
				return type.cast(new StringTextIn(lines[i]));
			}
			
			throw new UnsupportedeDataInException(getClass(), type);
		}
		
		@Override
		public String readLine() throws DataException {
			if (i == lines.length - 1) {
				return null;
			}
			else {
				return lines[++i];
			}
		}
	}
	
	public void testBindOutNoChild() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		LinesLayout root = new LinesLayout();
		root.setName("fruit");
		
		root.bind(test);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertFalse(writer.write("Apples"));
		
		List<String> results = dataOut.getLines();
		assertEquals(0, results.size());
		
		test.reset();
	}
	
	public void testBindInNoChild() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		LinesLayout root = new LinesLayout();
		root.setName("fruit");
		
		root.bind(test);
		
		OurLinesIn dataIn = new OurLinesIn(
				new String[] {"Apples", "Pears"});
		
		DataReader reader = root.readerFor(dataIn);
		
		Object o = reader.read();
		
		assertNotNull(o);
		assertEquals(Fruit.class, o.getClass());		
		
		assertNotNull(reader.read());
		assertNull(reader.read());
		
		test.reset();
	}
	
	
	public void testBindOutWithChilden() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");

		TextLayout typeNode = new TextLayout();
		typeNode.setName("type");
		
		LinesLayout lines = new LinesLayout();
		lines.setName("fruit");
		lines.setOf(0, typeNode);
		
		lines.bind(test);
		
		Fruit fruit = new Fruit();
		fruit.setType("apple");
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = lines.writerFor(dataOut);
		
		assertFalse(writer.write(fruit));
				
		List<String> results = dataOut.getLines();

		assertEquals("apple", results.get(0));
		assertEquals(1, results.size());
		
		test.reset();
	}

	public void testBindInWithChildren() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurLinesIn dataIn = new OurLinesIn(new String[] { "apple" });
		
		TextLayout typeNode = new TextLayout();
		typeNode.setName("type");
		
		LinesLayout root = new LinesLayout();
		root.setName("fruit");
		root.setOf(0, typeNode);
		
		root.bind(test);
		
		DataReader reader = root.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertNotNull(result);
		
		Fruit fruit = (Fruit) result;
		
		assertEquals("apple", fruit.getType());
		
		assertNull(reader.read());
		
		test.reset();
	}
	
	public static class Basket {
		
		private double cost;

		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}
	}
	
	public void testBindOutWithTypeConversion() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Basket.class));
		test.setNode("basket");
		
		TextLayout costNode = new TextLayout();
		costNode.setName("cost");
		
		LinesLayout root = new LinesLayout();
		root.setName("basket");
		root.setOf(0, costNode);
		
		root.bind(test);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		Basket basket = new Basket();
		basket.setCost(12.47);
		
		assertFalse(writer.write(basket));
		
		basket.setCost(5.23);
		
		assertFalse(writer.write(basket));
		
		List<String> results = dataOut.getLines();
		
		assertEquals("12.47", results.get(0));
		assertEquals("5.23", results.get(1));
		assertEquals(2, results.size());
		
		test.reset();
		
	}
	
	public void testBindInWithTypeConversion() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Basket.class));
		test.setNode("basket");
		
		TextLayout costNode = new TextLayout();
		costNode.setName("cost");
		
		LinesLayout root = new LinesLayout();
		root.setName("basket");
		root.setOf(0, costNode);
				
		root.bind(test);
		
		OurLinesIn dataIn = new OurLinesIn(new String[] { "12.47", "5.23" });
		
		DataReader reader = root.readerFor(dataIn);
		
		Object result = reader.read();
		
		assertNotNull(reader);
		
		Basket basket = (Basket) result;
		
		assertEquals(12.47, basket.getCost());
		
		result = reader.read();
		
		basket = (Basket) result;
		
		assertEquals(5.23, basket.getCost());
		
		result = reader.read();
		
		assertNull(result);
	}
	
	
	public void testBindOutMorphic() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		FieldLayout typeNode = new FieldLayout();
		typeNode.setName("type");
		
		DelimitedLayout root = new DelimitedLayout();
		root.setOf(0, typeNode);
		
		root.bind(test);
		
		ListLinesOut dataOut = new ListLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
				
		Fruit fruit = new Fruit();
		fruit.setType("apple");
		
		assertFalse(writer.write(fruit));
		
		List<String> results = dataOut.getLines();
		
		assertEquals("apple", results.get(0));
		assertEquals(1, results.size());
		
		fruit.setType("pear");
		
		assertFalse(writer.write(fruit));
		
		assertEquals("pear", results.get(1));
		assertEquals(2, results.size());
		
		test.reset();
	}

	public void testBindInMorphic() throws DataException {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		DelimitedLayout root = new DelimitedLayout();
		
		root.bind(test);
		
		OurLinesIn dataIn = new OurLinesIn(new String[] 
				{ "apple,27" , "pear,42" });
		
		DataReader reader = root.readerFor(dataIn);

		Object o = reader.read();
		
		Fruit fruit = (Fruit) o;
				
		assertEquals("apple", fruit.getType());
		
		o = reader.read();
		
		fruit = (Fruit) o;
				
		assertEquals("pear", fruit.getType());
		
		assertNull(reader.read());
		
		test.reset();
	}
	
}
