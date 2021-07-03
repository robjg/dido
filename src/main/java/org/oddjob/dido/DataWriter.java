package org.oddjob.dido;

/**
 * Something that data can be written to. This is one of the fundamental
 * concepts in Dido.
 * 
 * @see DataWriterFactory
 * @see DataReader
 * 
 * @author rob
 *
 */
public interface DataWriter {

	/**
	 * Write data.
	 * 
	 * @param object The data to be written.
	 * 
	 * @return Is more data required. If true then any calling 
	 * {@link Layout} should return to client code to be given a new value. 
	 * If false calling code should progress on to the next {@code Layout} 
	 * in the sequence of walking the {@code Layout} hierarchy.
	 * 
	 * @throws DataException
	 */
	public boolean write(Object object) throws DataException;
	
	/**
	 * Close the writer. This allows the writer to free resources or
	 * finalise writing of data. A writer should not be used again once
	 * it has been closed.
	 * 
	 * @throws DataException
	 */
	public void close() throws DataException;
}
