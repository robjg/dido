package org.oddjob.dido;


/**
 * Something that can create a {@link DataReader}. This is the starting
 * point for reading data.
 * 
 * @see DataWriterFactory
 * 
 * @author rob
 *
 */
trait DataReaderFactory {

	/**
	 * Create a reader.
	 * 
	 * @param dataIn The place that data will be read from.
	 * @return A reader. Never null.
	 * 
	 * @throws DataException If the reader can't be created.
	 */
	@throws(classOf[DataException])
	def readerFor(dataIn: DataIn): DataReader

}
