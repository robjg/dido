package org.oddjob.dido;

/**
 * Provides a means of writing data out.
 * 
 * @see DataNode.
 * 
 * @author rob
 *
 */
public interface DataOut {

	/**
	 * Write data back up the hierarchy. Should be called after 
	 * 
	 * @return True if data was written, false if it wasn't.
	 * 
	 * @throws DataException
	 */
	public boolean flush() throws DataException;
}
