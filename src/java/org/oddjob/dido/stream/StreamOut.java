package org.oddjob.dido.stream;

import java.io.OutputStream;

import org.oddjob.dido.DataOut;

/**
 * Writes data to a stream.
 * 
 * @author rob
 *
 */
public interface StreamOut extends DataOut {

	public OutputStream outputStream();
}
