package org.oddjob.dido;

/**
 * Provides a means of writing data out.
 * 
 * @see DataWriterFactory.
 * 
 * @author rob
 *
 */
public interface DataOut {

	<T extends DataOut> T provide(Class<T> type) 
	throws DataException;
	
	public boolean hasData();	
}
