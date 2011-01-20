package org.oddjob.dido.io;

/**
 * Something that traverses a tree of {@link DataNode}s and is willing
 * to tell you about it.
 * 
 * @author rob
 *
 */
public interface NodeWalker {
	
	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 */
	public void addListener(DIDOListener listener);
	
	/**
	 * Remove a {@link DIDOListener}.
	 * 
	 * @param listener
	 */
	public void removeListener(DIDOListener listener);
}
