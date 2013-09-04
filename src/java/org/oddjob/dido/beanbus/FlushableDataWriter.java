package org.oddjob.dido.beanbus;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataWriter;

/**
 * A data writer that supports batching or transactions or something else 
 * that needs data to be flushed.
 * 
 * @author rob
 *
 */
public interface FlushableDataWriter extends DataWriter {

	/**
	 * Flush the data.
	 * 
	 * @throws DataException
	 */
	public void flush() throws DataException;
}
