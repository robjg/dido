package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataInProvider;

/**
 * Provider of a line of text.
 * 
 * @author rob
 *
 */
public interface LinesIn extends DataIn, DataInProvider {

	/**
	 * Read a line of text.
	 * 
	 * @return A line of text or null if there is no more text.
	 * 
	 * @throws DataException
	 */
	public String readLine() throws DataException;
}
