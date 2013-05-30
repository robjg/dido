package org.oddjob.dido.io;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.Layout;

/**
 * Utility class for handling a tree of {@link Layouts}s.
 * 
 * @author rob
 *
 */
public class Nodes {

	private final Map<String, Layout> byName = 
		new HashMap<String, Layout>();
	
	/**
	 * Constructor.
	 * 
	 * @param root The root node of the tree.
	 */
	public Nodes(Layout root) {
		unpack(root);
	}
	
	/**
	 * Unpack the tree.
	 * 
	 * @param node
	 */
	private void unpack(Layout node) {
		String name = node.getName();
		if (name != null) {
			byName.put(name, node);
		}
		Iterable<Layout> children = node.childLayouts();
		for (Layout child : children) {
			unpack(child);
		}
	}
	
	/**
	 * Get a Node by name.
	 * 
	 * @param name The node name.
	 * 
	 * @return The node, or null if no node by name exists in the tree.
	 */
	public Layout getNode(String name) {
		return byName.get(name);
	}
}
