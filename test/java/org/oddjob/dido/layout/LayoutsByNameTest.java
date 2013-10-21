package org.oddjob.dido.layout;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.dido.Layout;
import org.oddjob.dido.MockLayout;

public class LayoutsByNameTest extends TestCase {

	private static final Logger logger = Logger.getLogger(LayoutsByNameTest.class);
	
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
		
		LayoutsByName test = new LayoutsByName(root);
		
		assertEquals("meals", test.getLayout("meals").getName());
		
		assertEquals("breakfast", test.getLayout("breakfast").getName());
		assertEquals("fruit", test.getLayout("breakfast:fruit").getName());
		assertEquals("apple", test.getLayout("breakfast:fruit:apple").getName());
		assertEquals("orange", test.getLayout("orange").getName());
		
		assertEquals("lunch", test.getLayout("lunch").getName());
		assertEquals("fruit", test.getLayout("lunch:fruit").getName());
		assertEquals("apple", test.getLayout("lunch:fruit:apple").getName());
		assertEquals("pear", test.getLayout("pear").getName());
		
		assertEquals(null, test.getLayout("fruit"));
		assertEquals(null, test.getLayout("apple"));
		
		assertEquals(9, test.size());
	}
	
	public void testUnresolveableDuplicatesFails() {
		
		Layout root = new OurLayout(null,
				new OurLayout(null,
						new OurLayout(null,
							new OurLayout("apple"),
							new OurLayout("orange")
						)
				),
				new OurLayout(null, 
						new OurLayout(null, 
							new OurLayout("apple"), 
							new OurLayout("pear")
						)
				)
		);
		
		try {
			new LayoutsByName(root);
			fail("This should fail.");
		}
		catch (IllegalStateException e) {
			// Expected
		}
		
	}
	
	public void testDuplicatesWithMissingInBettweenNodes() {
		
		Layout root = new OurLayout(null,
				new OurLayout("breakfast",
						new OurLayout(null,
							new OurLayout("apple"),
							new OurLayout("orange")
						)
				),
				new OurLayout("lunch", 
						new OurLayout(null, 
							new OurLayout("apple"), 
							new OurLayout("pear")
						)
				)
		);
		
		LayoutsByName test = new LayoutsByName(root);
		
		assertEquals("breakfast", test.getLayout("breakfast").getName());
		assertEquals("apple", test.getLayout("breakfast:apple").getName());
		assertEquals("orange", test.getLayout("orange").getName());
		
		assertEquals("lunch", test.getLayout("lunch").getName());
		assertEquals("apple", test.getLayout("lunch:apple").getName());
		assertEquals("pear", test.getLayout("pear").getName());
		
		assertEquals(null, test.getLayout("fruit"));
		assertEquals(null, test.getLayout("apple"));
		
		assertEquals(6, test.size());
	}
}
