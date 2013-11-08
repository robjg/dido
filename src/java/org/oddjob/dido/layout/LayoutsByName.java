package org.oddjob.dido.layout;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.dido.Layout;

/**
 * Utility class for handling a tree of {@link Layouts}s.
 * 
 * @author rob
 *
 */
public class LayoutsByName {
	
	private static final Logger logger = Logger.getLogger(LayoutsByName.class);

	private final Map<String, Container> byName = 
		new HashMap<String, Container>();
	
	/**
	 * Constructor.
	 * 
	 * @param root The root node of the tree.
	 */
	public LayoutsByName(Layout root) {
		unpack(new String[0], root);
	}
	
	/**
	 * Unpack the tree.
	 * 
	 * @param node
	 */
	private void unpack(String[] parentName, Layout node) {
	
		String[] newParentNames = parentName;
		
		String name = node.getName();
		
		if (name != null) {
			
			newParentNames = insert(new NamedContainer(name, new Container(parentName, node)));
							
			int last = parentName.length;
			newParentNames = new String[last + 1];
			
			System.arraycopy(parentName, 0, newParentNames, 0, last);
			newParentNames[last] = name;
		}
		
		Iterable<Layout> children = node.childLayouts();
		
		for (Layout child : children) {
			unpack(newParentNames, child);
		}
	}
	
	private String[] insert(NamedContainer namedContainer) {
		
		String name = namedContainer.name;
		if (name == null) {
			throw new NullPointerException("Name Null.");
		}
		
		while (true) {
			
			Container other = byName.get(name);
			if (other == null) {
				byName.put(name, namedContainer.container);
				logger.debug("Registering layout named [" + name + "]");
				return namedContainer.container.parents;
			}
			else {
				byName.remove(name);
				logger.debug("Removed duplicate layout named [" + name + "]");
				insert(new NamedContainer(name, other).shift());
				return insert(namedContainer.shift());
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
	public Layout getLayout(String name) {
		Container container = byName.get(name);
		if (container == null) {
			return null;
		}
		else {
			return container.layout;
		}
	}
	
	/**
	 * Get the total number of nodes.
	 * 
	 * @return The total.
	 */
	public int size() {
		return byName.size();
	}
	
	/**
	 * Get all the entries. This copies the entries into a new Map.
	 * 
	 * @return All.
	 */
	public Map<String, Layout> getAll() {
		Map<String, Layout> all = new HashMap<String, Layout>();
		for (Map.Entry<String, Container> entry : byName.entrySet()) {
			all.put(entry.getKey(), entry.getValue().layout);
		}
		return all;
	}
	
	private class Container {
		
		private final String[] parents;
		private final Layout layout;
		
		public Container(String[] parents, Layout node) {
			this.parents = parents;
			this.layout = node;
		}
	}
	
	private class NamedContainer {
		
		private final String name;
		private final Container container;
		
		public NamedContainer(String name, Container container) {
			this.name = name;
			this.container = container;
		}
		
		public NamedContainer shift() {
			
			if (container.parents.length == 0) {
				throw new IllegalStateException("No unique name available for " + 
						name);
			}
			
			int last = container.parents.length - 1;
			String newName = container.parents[last] + ":" + name;
			String[] newParentNames = new String[last];
			System.arraycopy(container.parents, 0, newParentNames, 0, 
					last);
			
			return new NamedContainer(newName, new Container(
					newParentNames, container.layout));
		}
	}
}
