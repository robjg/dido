package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;

/**
 * Something that can be linked to, to provide data as it is being read in.
 * 
 * @author rob
 *
 */
public interface LinkableIn {
	
	/**
	 * Set a dataLink for a particular node.
	 * 
	 * @param node The node. Must not be null.
	 * @param dataLink The link. Will overwrite any existing link and if 
	 * null will remove any existing link.
	 */
	public void setControlIn(DataNode<?, ?, ?, ?> node, DataLinkIn dataLink);

}
