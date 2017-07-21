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
trait DataIn {

	/**
	 * Provide a {@link DataIn} out of the desired type.
	 * 
	 * @param theType The type.
	 * @return A [[DataIn]] of the desired type.
	 * 
	 * @throws UnsupportedDataOutException If the implementation does not
	 * support the desired Data type.
	 * @throws DataException Any thing else that went wrong.
	 */
	@throws(classOf[DataException])
	def provideDataIn[T <: DataIn](theType: Class[T]): T

	
}
