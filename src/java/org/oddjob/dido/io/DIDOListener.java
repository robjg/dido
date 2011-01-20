package org.oddjob.dido.io;

/**
 * Listen to {@link DataNode} traversal by a {@link NodeWalker}.
 * 
 * @author rob
 *
 */
public interface DIDOListener {

	/**
	 * Start of the node. If the node is a {@link Stencil} it will have
	 * it's value set at this point.
	 * 
	 * @param event
	 */
	public void startNode(DIDOEvent event);
	
	/**
	 * End of processing the node.
	 * 
	 * @param event
	 */
	public void endNode(DIDOEvent event);
	
}
