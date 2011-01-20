package org.oddjob.dido.bio;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.io.LinkableIn;

/**
 * Provides a binding for reading data between the thing thats doing
 * the reading and the structure of data nodes it's populating.
 * 
 * @author rob
 *
 */
public interface BindingIn {

	/**
	 * Bind to the node via the thing that we can link to (the 
	 * reader normally).
	 * 
	 * @param linkNode
	 * @param linkable
	 */
	public void bindTo(DataNode<?, ?, ?, ?> linkNode, LinkableIn linkable);

}
