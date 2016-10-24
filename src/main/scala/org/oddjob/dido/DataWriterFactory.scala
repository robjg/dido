package org.oddjob.dido;

/**
 * Something that can create a [[DataWriter]]
 * . This is the starting
 * point for writing data.
 * 
 * @see DataReaderFactory
 * 
 * @author rob
 *
 */
trait DataWriterFactory {

	/**
	 * Create a writer.
	 * 
	 * @param dataOut The place that data will be written to.
	 * @return A writer. Never null.
	 * 
	 * @throws DataException If the writer can't be created.
	 */
	@throws(classOf[DataException])
	def writerFor(dataOut: DataOut): DataWriter
}
