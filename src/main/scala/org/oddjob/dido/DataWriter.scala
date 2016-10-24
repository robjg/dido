package org.oddjob.dido

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
trait DataWriter {

	/**
	 * Write data.
	 * 
	 * @param value The data to be written.
	 * 
	 * @return Is more data required. If true then any calling 
	 * [[Layout]] should return to client code to be given a new value.
	 * If false calling code should progress on to the next {{{Layout}}}
	 * in the sequence of walking the {{{Layout}}} hierarchy.
	 * 
	 * @throws DataException
	 */
	@throws(classOf[DataException])
	def write(value: Any): Boolean
	
	/**
	 * Close the writer. This allows the writer to free resources or
	 * finalise writing of data. A writer should not be used again once
	 * it has been closed.
	 * 
	 * @throws DataException
	 */
	@throws(classOf[DataException])
	def close(): Unit
}
