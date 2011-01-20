package org.oddjob.dido.io;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.SupportsChildren;

/**
 * Utility class for handling a tree of {@link DataNode}s.
 * 
 * @author rob
 *
 */
public class Nodes {

	private final Map<String, DataNode<?, ?, ?, ?>> byName = 
		new HashMap<String, DataNode<?, ?, ?, ?>>();
	
	/**
	 * Constructor.
	 * 
	 * @param root The root node of the tree.
	 */
	public Nodes(DataNode<?, ?, ?, ?> root) {
		unpack(root);
	}
	
	/**
	 * Unpack the tree.
	 * 
	 * @param node
	 */
	private void unpack(DataNode<?, ?, ?, ?> node) {
		String name = node.getName();
		if (name != null) {
			byName.put(name, node);
		}
		if (node instanceof SupportsChildren) {
			for (DataNode<?, ?, ?, ?> child : 
					((SupportsChildren) node).childrenToArray()) {
				unpack(child);
			}
		}
	}
	
	/**
	 * Get a Node by name.
	 * 
	 * @param name The node name.
	 * 
	 * @return The node, or null if no node by name exists in the tree.
	 */
	public DataNode<?, ?, ?, ?> getNode(String name) {
		return byName.get(name);
	}
}
