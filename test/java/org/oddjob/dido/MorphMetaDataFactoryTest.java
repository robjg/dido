package org.oddjob.dido;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanViewBean;

public class MorphMetaDataFactoryTest extends TestCase {

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
		
		public boolean isNice() {
			return true;
		}
		
		public void setEaten(boolean eaten) {
			
		}
	}
	
	public void testReadableMorphMetaData() {
		
		MorphMetaDataFactory test = new MorphMetaDataFactory(
				new BeanUtilsPropertyAccessor());
		
		MorphMetaData result = test.readableMorphMetaDataFor(
				new SimpleArooaClass(Fruit.class));
		
		Map<String, Class<?>> types = new HashMap<String, Class<?>>();
		
		String[] names = result.getNames();

		for (String name : names) {
			
			assertEquals(name, result.titleFor(name));
			types.put(name, result.typeOf(name));
		}
		
		assertEquals(true, types.containsKey("type"));
		assertEquals(true, types.containsKey("quantity"));
		assertEquals(true, types.containsKey("nice"));
		
		assertEquals(3, types.size());

		assertEquals(String.class, types.get("type"));
		assertEquals(int.class, types.get("quantity"));
		assertEquals(boolean.class, types.get("nice"));

	}
	
	
	public void testReadWithBeanView() {
		
		MorphMetaDataFactory test = new MorphMetaDataFactory(
				new BeanUtilsPropertyAccessor());
		
		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("type, quantity, nice, eaten");
		
		MorphMetaData morphicness = test.readableMorphMetaDataFor(
				new SimpleArooaClass(Fruit.class), beanView.toValue());
		
		String[] names = morphicness.getNames();
		
		assertEquals("type", names[0]);
		assertEquals("quantity", names[1]);
		assertEquals("nice", names[2]);
		
		assertEquals(3, names.length);
	}

	
	public void testWriteMorphMetaData() {
		
		MorphMetaDataFactory test = new MorphMetaDataFactory(
				new BeanUtilsPropertyAccessor());
		
		MorphMetaData result = test.writeableMorphMetaDataFor(
				new SimpleArooaClass(Fruit.class));
		
		Map<String, Class<?>> types = new HashMap<String, Class<?>>();
		
		String[] names = result.getNames();

		for (String name : names) {
			
			assertEquals(name, result.titleFor(name));
			types.put(name, result.typeOf(name));
		}
		
		assertEquals(true, types.containsKey("type"));
		assertEquals(true, types.containsKey("quantity"));
		assertEquals(true, types.containsKey("eaten"));
		
		assertEquals(3, types.size());

		assertEquals(String.class, types.get("type"));
		assertEquals(int.class, types.get("quantity"));
		assertEquals(boolean.class, types.get("eaten"));

	}
	
	public void testWriteWithBeanView() {
		
		MorphMetaDataFactory test = new MorphMetaDataFactory(
				new BeanUtilsPropertyAccessor());
		
		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("type, quantity, nice, eaten");
		
		MorphMetaData morphicness = test.writeableMorphMetaDataFor(
				new SimpleArooaClass(Fruit.class), beanView.toValue());
		
		String[] names = morphicness.getNames();
		
		assertEquals("type", names[0]);
		assertEquals("quantity", names[1]);
		assertEquals("eaten", names[2]);

		assertEquals(3, names.length);
	}
}
