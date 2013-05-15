package org.oddjob.dido.bio;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataNode;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.MockDataNode;
import org.oddjob.dido.ValueNode;
import org.oddjob.dido.SupportsChildren;
import org.oddjob.dido.io.ClassMorphic;
import org.oddjob.dido.io.DataLinkIn;
import org.oddjob.dido.io.DataLinkOut;
import org.oddjob.dido.io.LinkInControl;
import org.oddjob.dido.io.LinkInEvent;
import org.oddjob.dido.io.LinkOutEvent;

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
	
	private class OurNode extends MockDataNode<DataIn, DataIn, DataOut, DataOut> {
		
		private final String name;
		
		 public OurNode(String name) {
			 this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
	}
	
	private class OurLinkableOut extends MockLinkableOut {
		
		private List<DataLinkOut> links = new ArrayList<DataLinkOut>();
		
		@Override
		public void setLinkOut(DataNode<?, ?, ?, ?> node, DataLinkOut link) {
			if (node == null) {
				throw new NullPointerException("node.");
			}
			if (link == null) {
				throw new NullPointerException("link.");
			}
			links.add(link);
		}
	}
	
	
	public void testBindOutNoChild() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurNode root = new OurNode("fruit");
		
		OurLinkableOut linkable = new OurLinkableOut();
		
		test.bindTo(root, linkable);
		
		assertEquals(1, linkable.links.size());
		
		boolean control = linkable.links.get(0).dataOut(
				new LinkOutEvent(linkable, root), new Fruit());
		
		assertTrue(control);
		
	}
	
	private class OurLinkableIn extends MockLinkableIn {
		
		private List<DataLinkIn> links = new ArrayList<DataLinkIn>();

		@Override
		public void setControlIn(DataNode<?, ?, ?, ?> node, DataLinkIn link) {
			if (node == null) {
				throw new NullPointerException("node.");
			}
			if (link == null) {
				throw new NullPointerException("link.");
			}
			links.add(link);
		}
	}
	
	public void testBindInNoChild() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurNode root = new OurNode("fruit");
		
		OurLinkableIn linkable = new OurLinkableIn();
		
		test.bindTo(root, linkable);
		
		assertEquals(1, linkable.links.size());
		
		LinkInControl control = linkable.links.get(0).dataIn(
				new LinkInEvent(linkable, root));
		
		Object o = control.getDataObject();
		
		assertNotNull(o);
		assertEquals(Fruit.class, o.getClass());		
	}
	
	private class OurParentNode extends OurNode implements SupportsChildren {
		
		private DataNode<?, ?, ?, ?>[] children;
		
		public OurParentNode(String name, DataNode<?, ?, ?, ?>... children) {
			super(name);
			this.children = children;
		}
		
		void setChildren(DataNode<?, ?, ?, ?>[] children) {
			this.children = children;
		}
		
		@Override
		public DataNode<?, ?, ?, ?>[] childrenToArray() {
			return children;
		}
	}

	private class OurStencilNode 
	extends MockDataNode<DataIn, DataIn, DataOut, DataOut>
	implements ValueNode<String> {
		
		private final String name;
		
		private String value;
		
		 public OurStencilNode(String name) {
			 this.name = name;
		}
		
		 public OurStencilNode(String name, String value) {
			 this.name = name;
			 this.value = value;
		}
		 
		@Override
		public Class<String> getType() {
			return String.class;
		}
		 
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public void value(String value) {
			this.value = value;
		}
	}
	
	public void testBindOutWithChilden() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurStencilNode typeNode = new OurStencilNode("type");
		
		OurParentNode root = new OurParentNode("fruit",
				typeNode,
				new OurStencilNode("colour"));
		
		OurLinkableOut linkable = new OurLinkableOut();
		
		test.bindTo(root, linkable);
		
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

	public void testBindInWithChildren() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Fruit.class));
		test.setNode("fruit");
		
		OurStencilNode typeNode = new OurStencilNode("type", "apple");
		
		OurParentNode root = new OurParentNode("fruit",
				typeNode,
				new OurStencilNode("colour", "red"));
		
		OurLinkableIn linkable = new OurLinkableIn();
		
		test.bindTo(root, linkable);
		
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
	
	public static class Basket {
		
		private double cost;

		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}
	}
	
	public void testBindOutWithTypeConversion() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Basket.class));
		test.setNode("basket");
		
		OurStencilNode costNode = new OurStencilNode("cost");
		
		OurParentNode root = new OurParentNode("basket",
				costNode);
		
		OurLinkableOut linkable = new OurLinkableOut();
		
		test.bindTo(root, linkable);
		
		assertEquals(2, linkable.links.size());
		
		Basket basket = new Basket();
		basket.setCost(12.47);
		
		boolean control = linkable.links.get(0).dataOut(new LinkOutEvent(
				linkable, root), basket);
		
		assertTrue(control);
		assertEquals(null, costNode.value());
		
		control = linkable.links.get(1).dataOut(new LinkOutEvent(
				linkable, costNode), basket);
		
		assertTrue(control);
		assertEquals("12.47", costNode.value());		
	}
	
	public void testBindInWithTypeConversion() {
		
		StandardArooaSession session = new StandardArooaSession();
		
		BeanBindingBean test = new BeanBindingBean();
		test.setArooaSession(session);
		test.setType(new SimpleArooaClass(Basket.class));
		test.setNode("basket");
		
		OurStencilNode costNode = new OurStencilNode("cost", "12.47");
		
		OurParentNode root = new OurParentNode("basket",
				costNode);
		
		OurLinkableIn linkable = new OurLinkableIn();
		
		test.bindTo(root, linkable);
		
		assertEquals(2, linkable.links.size());
		
		LinkInControl control = linkable.links.get(0).dataIn(
				new LinkInEvent(linkable, root));
		
		Object o = control.getDataObject();
		
		assertNotNull(o);
		
		Basket basket = (Basket) o;
		
		assertEquals(0.0, basket.getCost());
		
		control = linkable.links.get(1).dataIn(
				new LinkInEvent(linkable, costNode));
		
		assertEquals(12.47, basket.getCost());
	}
	
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
}
