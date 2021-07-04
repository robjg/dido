package org.oddjob.dido;

import org.oddjob.dido.other.Case;
import org.oddjob.dido.other.When;

/**
 * Provides a means of writing data out.
 * 
 * @see DataWriterFactory.
 * @see DataIn.
 * 
 * @author rob
 *
 */
public interface DataOut {

	/**
	 * Provide a {@link DataOut} out of the desired type.
	 * 
	 * @param type The type.
	 * @return A {@link DataOut} of the desired type.
	 * 
	 * @throws UnsupportedDataOutException If the implementation does not
	 * support the desired Data type.
	 * @throws DataException Any thing else that went wrong.
	 */
	<T extends DataOut> T provideDataOut(Class<T> type) 
	throws DataException;
	
	/**
	 * Indicate if this data has been written out to in the context of the 
	 * most recent write operation. This is used by {@link Case} to know
	 * that a particular {@link When} has been used, and also
	 * internally by some writers.
	 * <p>
	 * Implementations will often provide a <code>resetWrittenTo()</code> 
	 * method for the use of a {@link DataWriter} but this is an 
	 * implementation choice.
	 * <p>
	 * This method is optional because it makes no sense in some contexts
	 * particularly root data.
	 * 
	 * @return
	 * 
	 * @throws UnsupportedOperationException
	 */
	public boolean isWrittenTo() throws UnsupportedOperationException;
}
