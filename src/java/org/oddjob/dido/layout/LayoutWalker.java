package org.oddjob.dido.layout;

import org.oddjob.dido.Layout;

abstract public class LayoutWalker {

	public void walk(Layout layout) {
		
		Iterable<Layout> children = layout.childLayouts();

		for (Layout child : children) {
			if (onLayout(child)) {
				walk(child);
			}
			
		}
	}
	
	abstract protected boolean onLayout(Layout layout);
	
	
}
