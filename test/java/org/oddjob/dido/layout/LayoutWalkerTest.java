package org.oddjob.dido.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.dido.Layout;
import org.oddjob.dido.MockLayout;

/**
 * 
 * @author rob
 *
 */
public class LayoutWalkerTest extends TestCase {

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
	
	public void testWalksLayout() {
		
		Layout root = new OurLayout("meals", 
				new OurLayout("breakfast", 
						new OurLayout("fruit", 
							new OurLayout("apple"), 
							new OurLayout("orange")
						)
				),
				new OurLayout("lunch", 
						new OurLayout("salad", 
							new OurLayout("tomato"), 
							new OurLayout("lettuce")
						)
				)
		);
		
		final List<String> names = new ArrayList<String>();
		
		LayoutWalker test = new LayoutWalker() {
			
			@Override
			protected boolean onLayout(Layout layout) {
				names.add(layout.getName());
				return true;
			}
		};
		
		test.walk(root);
		
		assertEquals("meals", names.get(0));
		assertEquals("breakfast", names.get(1));
		assertEquals("fruit", names.get(2));
		assertEquals("apple", names.get(3));
		assertEquals("orange", names.get(4));
		assertEquals("lunch", names.get(5));
		assertEquals("salad", names.get(6));
		assertEquals("tomato", names.get(7));
		assertEquals("lettuce", names.get(8));
	}
}
