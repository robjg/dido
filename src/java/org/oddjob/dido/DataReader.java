package org.oddjob.dido;


public interface DataReader {

	public Object read() throws DataException;
	
	public void close() throws DataException;
}
