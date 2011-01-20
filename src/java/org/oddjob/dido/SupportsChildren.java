package org.oddjob.dido;

/**
 * An object that supports child {@link DataNode}s.
 * 
 * @author rob
 *
 */
public interface SupportsChildren {

	/**
	 * Return the children as an array. 
	 * 
	 * @return An array. Must not be null.
	 */
	public DataNode<?, ?, ?, ?>[] childrenToArray();
}
