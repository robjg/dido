package org.oddjob.dido.stream;

import java.io.OutputStream;

/**
 * Writes data to a stream.
 * 
 * @author rob
 *
 */
public interface StreamOut extends LinesOut {

	public OutputStream getStream();
}
