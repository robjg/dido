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
public interface DataReaderFactory {

	/**
	 * Create a reader.
	 * 
	 * @param dataOut The place that data will be read from.
	 * @return A reader. Never null.
	 * 
	 * @throws DataException If the reader can't be created.
	 */
	public DataReader readerFor(DataIn dataIn)
	throws DataException;
	
}
