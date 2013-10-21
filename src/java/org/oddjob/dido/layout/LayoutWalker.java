package org.oddjob.dido.layout;

import org.oddjob.dido.Layout;

/**
 * Helper class to walk the nodes in a hierarchy of {@link Layouts}. This
 * walker does a depth first walk of the tree.
 * 
 * @author rob
 *
 */
abstract public class LayoutWalker {

	/**
	 * Walk a node and its children. 
	 * 
	 * @param layout The root layout.
	 */
	public void walk(Layout layout) {
		if (onLayout(layout)) {
			walkChildren(layout);
		}
	}
	
	/**
	 * Walk the children of a Layout. the {@link #onLayout(Layout)} method
	 * will not be called for the root layout node.
	 * 
	 * @param layout The root layout node.
	 */
	public void walkChildren(Layout layout) {
		
		Iterable<Layout> children = layout.childLayouts();

		for (Layout child : children) {
			if (onLayout(child)) {
				walkChildren(child);
			}
			
		}
	}
	
	/**
	 * 
	 * @param layout
	 * 
	 * @return true to walk children, false to not walk children.
	 */
	abstract protected boolean onLayout(Layout layout);
	
	
}
