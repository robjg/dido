package org.oddjob.dido;

/**
 * Provides a means of writing data out.
 * 
 * @see DataWriterFactory.
 * @see DataIn.
 * 
 * @author rob
 *
 */
trait DataOut {

	/**
	 * Provide a {@link DataOut} out of the desired type.
	 * 
	 * @param theType The type.
	 * @return A [[DataOut]] of the desired type.
	 * 
	 * @throws UnsupportedDataOutException If the implementation does not
	 * support the desired Data type.
	 * @throws DataException Any thing else that went wrong.
	 */
	@throws(classOf[DataException])
	def provideDataOut[T <: DataOut](theType: Class[T]): T

	/**
	 * Indicate if this data has been written out to in the context of the 
	 * most recent write operation. This is used by [[Case]] to know
	 * that a particular [[When]] has been used, and also
	 * internally by some writers.
	 * <p>
	 * Implementations will often provide a <code>resetWrittenTo()</code> 
	 * method for the use of a [[DataWriter]] but this is an
	 * implementation choice.
	 * <p>
	 * This method is optional because it makes no sense in some contexts
	 * particularly root data.
	 * 
	 * @return
	 * 
	 * @throws UnsupportedOperationException
	 */
	@throws(classOf[UnsupportedOperationException])
	def isWrittenTo(): Boolean
}
