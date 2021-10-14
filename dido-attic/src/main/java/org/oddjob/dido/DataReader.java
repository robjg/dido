package org.oddjob.dido;


/**
 * Something that data provide data by being read. This is one of the 
 * fundamental concepts in Dido.
 * 
 * @see DataReaderFactory
 * @see DataWriter
 * 
 * @author rob
 *
 */
public interface DataReader {

	/**
	 * Read data.
	 * 
	 * @return The data read. If not null then any calling 
	 * {@link Layout} should allow client code to call the reader again if
	 * necessary to be given more data. If a reader returns null
	 * the calling code should progress on to the next {@code Layout} 
	 * in the sequence of walking the {@code Layout} hierarchy to read data. 
	 * If null is returned from the root {@code Layout} then there is
	 * no more data to be read.
	 * 
	 * @throws DataException
	 */
	public Object read() throws DataException;
	
	/**
	 * Close the reader. Allows the reader to free resources. A closed reader
	 * should not be read from again.
	 * 
	 * @throws DataException
	 */
	public void close() throws DataException;
}
