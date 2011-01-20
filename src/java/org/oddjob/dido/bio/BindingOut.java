package org.oddjob.dido.bio;

import org.oddjob.dido.DataNode;
import org.oddjob.dido.io.LinkableOut;

/**
 * Provide a way of binding to a data structure of the given root node
 * and the thing thats writing data via that structure.
 * 
 * @author rob
 *
 */
public interface BindingOut {

	/**
	 * Bind to a data definition tree via the thing we can link to 
	 * (the writer normally).
	 * 
	 * @param definitionRoot The root of the definition tree.
	 * @param linkable The thing that is the link to the data.
	 */
	public void bindTo(DataNode<?, ?, ?, ?> definitionRoot, 
			LinkableOut linkable);

}
