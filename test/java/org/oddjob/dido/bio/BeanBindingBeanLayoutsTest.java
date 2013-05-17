package org.oddjob.dido.bio;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.MockDataOut;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.stream.LinesIn;
import org.oddjob.dido.stream.LinesLayout;
import org.oddjob.dido.stream.LinesOut;
import org.oddjob.dido.text.TextLayout;

public class BeanBindingBeanLayoutsTest extends TestCase {

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
		public <T extends DataOut> T provideOut(Class<T> type)
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
	
	private class OurLinesIn 
	implements LinesIn {

		final String[] lines;
		
		int i = 0;
		
		public OurLinesIn(String[] lines) {
			this.lines = lines;
		}
		
		@Override
		public <T extends DataIn> T provideIn(Class<T> type)
				throws UnsupportedeDataInException {
			if (type.isAssignableFrom(LinesIn.class)) {
				return type.cast(this);
			}
			else {
				throw new UnsupportedeDataInException(getClass(), type);
			}
		}
		
		@Override
		public String readLine() throws DataException {
			if (i >= lines.length) {
				return null;
			}
			else {
				return lines[i++];
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
		
		OurLinesOut dataOut = new OurLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		assertFalse(writer.write("Apples"));
		
		assertEquals(0, dataOut.results.size());
		
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
		
		OurLinesOut dataOut = new OurLinesOut();
		
		DataWriter writer = lines.writerFor(dataOut);
		
		assertFalse(writer.write(fruit));
				
		assertEquals("apple", dataOut.results.get(0));
		assertEquals(1, dataOut.results.size());
		
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
		
		OurLinesOut dataOut = new OurLinesOut();
		
		DataWriter writer = root.writerFor(dataOut);
		
		Basket basket = new Basket();
		basket.setCost(12.47);
		
		assertFalse(writer.write(basket));
		
		basket.setCost(5.23);
		
		assertFalse(writer.write(basket));
		
		assertEquals("12.47", dataOut.results.get(0));
		assertEquals("5.23", dataOut.results.get(1));
		assertEquals(2, dataOut.results.size());
		
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
	
	/**
	
	private class OurMorphicNode extends OurParentNode 
	implements ClassMorphic {

		ArooaClass arooaClass;
		
		private DataNode<?, ?, ?, ?>[] children;
		
		public OurMorphicNode(String name, DataNode<?, ?, ?, ?>... children) {
			super(name);
			this.children = children;
		}
		
		@Override
		public void beFor(ArooaClass arooaClass) {
			setChildren(children);
			this.arooaClass = arooaClass;
		}
	}
	
	public void testBindOutMorphic() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurStencilNode typeNode = new OurStencilNode("type");
		
		OurMorphicNode root = new OurMorphicNode("fruit",
				typeNode,
				new OurStencilNode("colour"));
		
		OurLinkableOut linkable = new OurLinkableOut();
		
		test.bindTo(root, linkable);
		
		assertEquals(Fruit.class, root.arooaClass.forClass());
		
		assertEquals(2, linkable.links.size());
		
		Fruit fruit = new Fruit();
		fruit.setType("apple");
		
		boolean control = linkable.links.get(0).dataOut(new LinkOutEvent(
				linkable, root), fruit);
		
		assertTrue(control);
		assertEquals(null, typeNode.value());
		
		control = linkable.links.get(1).dataOut(new LinkOutEvent(
				linkable, typeNode), fruit);
		
		assertTrue(control);
		assertEquals("apple", typeNode.value());
	}

	public void testBindInMorphic() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurStencilNode typeNode = new OurStencilNode("type", "apple");
		
		OurMorphicNode root = new OurMorphicNode("fruit",
				typeNode,
				new OurStencilNode("colour", "red"));
		
		OurLinkableIn linkable = new OurLinkableIn();
		
		test.bindTo(root, linkable);
		
		assertEquals(Fruit.class, root.arooaClass.forClass());
		
		assertEquals(2, linkable.links.size());
		
		LinkInControl control = linkable.links.get(0).dataIn(
				new LinkInEvent(linkable, root));
		
		Object o = control.getDataObject();
		
		assertNotNull(o);
		
		Fruit fruit = (Fruit) o;
		
		assertEquals(null, fruit.getType());
		
		control = linkable.links.get(1).dataIn(
				new LinkInEvent(linkable, typeNode));
		
		assertEquals("apple", fruit.getType());
	}
	
	*/
}
