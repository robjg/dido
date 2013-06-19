package org.oddjob.dido.text;

import org.oddjob.dido.DataOut;

/**
 * A {@link DataOut} for text.
 * 
 * @author rob
 *
 */
public interface TextOut extends DataOut {

	public void append(String text);
	
	public void write(String text, int from, int length);
	
}
