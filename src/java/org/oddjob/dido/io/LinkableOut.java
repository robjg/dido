package org.oddjob.dido.io;

import org.oddjob.dido.DataNode;

/**
 * Supports a link for the purpose of writing data out.
 * 
 * @author rob
 *
 */
public interface LinkableOut {

	/**
	 * Set a link out for the given {@link DataNode}.
	 * 
	 * @param node The node. Must not be null.
	 * @param link The link. This link will overwrite any existing link, if
	 * null, it will remove any existing link.
	 */
	public void setLinkOut(DataNode<?, ?, ?, ?> node, DataLinkOut link);
}
