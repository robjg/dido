package org.oddjob.dido.stream;

import java.io.InputStream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;

/**
 * Reads data from a stream.
 * 
 * @author rob
 *
 */
public interface StreamIn extends DataIn {

	public InputStream inputStream();
	
	public void close() throws DataException;
}
