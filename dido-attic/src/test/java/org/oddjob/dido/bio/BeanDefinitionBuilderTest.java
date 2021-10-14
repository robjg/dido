package org.oddjob.dido.bio;

import junit.framework.TestCase;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.BeanView;

public class BeanDefinitionBuilderTest extends TestCase {

	public void testBuildWithReplacements() {
		
		BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
		
		builder.addProperty("Fruit", String.class);
		builder.addProperty("Flavour (taste)", String.class);
		builder.addProperty("Weight (lbs.)", String.class);
		builder.addProperty("[Qty]", String.class);
		builder.addProperty("_Qty_", String.class);
		
		ArooaClass type = builder.createType();
		
		BeanOverview overview = type.getBeanOverview(null);
		
		String[] properties = overview.getProperties();

		assertEquals("Fruit", properties[0]);
		assertEquals("Flavour _taste_", properties[1]);
		assertEquals("Weight _lbs__", properties[2]);
		assertEquals("_Qty_", properties[3]);
		assertEquals("_Qty__", properties[4]);
		
		assertEquals(String.class, overview.getPropertyType("Fruit"));

		
		BeanView view = builder.createBeanView();
		
		assertEquals("Fruit", view.titleFor("Fruit"));
		assertEquals("Flavour (taste)", view.titleFor("Flavour _taste_"));
		assertEquals("Weight (lbs.)", view.titleFor("Weight _lbs__"));
		assertEquals("[Qty]", view.titleFor("_Qty_"));
		assertEquals("_Qty_", view.titleFor("_Qty__"));
	}
}
