package org.oddjob.dido;

/**
 * Provides a means of reading data.
 * 
 * @see DataReaderFactory.
 * @see DataOut
 * 
 * @author rob
 *
 */
public interface DataIn {

	/**
	 * Provide a {@link DataIn} out of the desired type.
	 * 
	 * @param type The type.
	 * @return A {@link DataIn} of the desired type.
	 * 
	 * @throws UnsupportedDataOutException If the implementation does not
	 * support the desired Data type.
	 * @throws DataException Any thing else that went wrong.
	 */
	<T extends DataIn> T provideDataIn(Class<T> type) 
	throws DataException;
	
}
