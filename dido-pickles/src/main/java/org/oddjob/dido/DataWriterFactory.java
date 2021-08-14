package org.oddjob.dido;

/**
 * Something that can create a {@link DataWriter}. This is the starting
 * point for writing data.
 * 
 * @see DataReaderFactory
 * 
 * @author rob
 *
 */
public interface DataWriterFactory {

	/**
	 * Create a writer.
	 * 
	 * @param dataOut The place that data will be written to.
	 * @return A writer. Never null.
	 * 
	 * @throws DataException If the writer can't be created.
	 */
	DataWriter writerFor(DataOut dataOut)
	throws DataException;
}
