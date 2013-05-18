package org.oddjob.dido;


public interface DataReaderFactory {

	public DataReader readerFor(DataIn dataIn)
	throws DataException;
	
}
