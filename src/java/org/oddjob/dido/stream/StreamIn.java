package org.oddjob.dido.stream;

import java.io.InputStream;

/**
 * Reads data from a stream.
 * 
 * @author rob
 *
 */
public interface StreamIn extends LinesIn {


	public InputStream getStream();
	
}
