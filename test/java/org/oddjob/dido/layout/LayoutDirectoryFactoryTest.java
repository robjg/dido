package org.oddjob.dido.layout;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.dido.Layout;
import org.oddjob.dido.MockLayout;

public class LayoutDirectoryFactoryTest extends TestCase {

	private static final Logger logger = Logger.getLogger(LayoutDirectoryFactoryTest.class);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		logger.info("-------------------------------  " + getName() + 
				"  --------------------------------");
	}
	
	private class OurLayout extends MockLayout {
		
		String name;
		List<Layout> children;
		
		public OurLayout(String name, Layout... children) {
			
			this.name = name;
			this.children = Arrays.asList(children);
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public List<Layout> childLayouts() {
			return children;
		}
	}
	
	public void testNodeTreeWithDuplicateNameOfNamedBranch() {
		
		Layout root = new OurLayout("meals",
				new OurLayout("breakfast",
						new OurLayout("fruit",
							new OurLayout("apple"),
							new OurLayout("orange")
						)
				),
				new OurLayout("lunch", 
						new OurLayout("fruit", 
							new OurLayout("apple"), 
							new OurLayout("pear")
						)
				)
		);
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		ArooaConverter converter = new DefaultConverter();
		
		LayoutDirectoryFactory test = new LayoutDirectoryFactory(accessor, 
				converter);
		
		BeanDirectory directory = test.createFrom(root);
		
		assertEquals("meals", directory.lookup("meals.name"));
		
		assertEquals("breakfast", directory.lookup("breakfast.name"));
		assertEquals("fruit", directory.lookup("breakfast:fruit.name"));
		assertEquals("apple", directory.lookup("breakfast:fruit:apple.name"));
		assertEquals("orange", directory.lookup("orange.name"));
		
		assertEquals("lunch", directory.lookup("lunch.name"));
		assertEquals("fruit", directory.lookup("lunch:fruit.name"));
		assertEquals("apple", directory.lookup("lunch:fruit:apple.name"));
		assertEquals("pear", directory.lookup("pear.name"));
		
		assertEquals(null, directory.lookup("fruit"));
		assertEquals(null, directory.lookup("apple"));		
	}
	
}
