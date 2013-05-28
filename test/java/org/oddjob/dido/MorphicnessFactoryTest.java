package org.oddjob.dido;

import junit.framework.TestCase;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.BeanViewBean;

public class MorphicnessFactoryTest extends TestCase {

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
	
	public void testReadWithBeanView() {
		
		MorphicnessFactory test = new MorphicnessFactory(
				new BeanUtilsPropertyAccessor());
		
		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("type, quantity");
		
		Morphicness morphicness = test.readMorphicnessFor(
				new SimpleArooaClass(Fruit.class), beanView.toValue());
		
		String[] names = morphicness.getNames();
		
		assertEquals("type", names[0]);
		assertEquals("quantity", names[1]);
	}

	
	public void testWriteWithBeanView() {
		
		MorphicnessFactory test = new MorphicnessFactory(
				new BeanUtilsPropertyAccessor());
		
		BeanViewBean beanView = new BeanViewBean();
		beanView.setProperties("type, quantity");
		
		Morphicness morphicness = test.writeMorphicnessFor(
				new SimpleArooaClass(Fruit.class), beanView.toValue());
		
		String[] names = morphicness.getNames();
		
		assertEquals("type", names[0]);
		assertEquals("quantity", names[1]);
	}
}
