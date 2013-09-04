package org.oddjob.dido.stream;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;

/**
 * Something that can write lines of text.
 * 
 * @author rob
 *
 */
public interface LinesOut extends DataOut {

	/**
	 * Write a line of text.
	 * 
	 * @param text The text. Shouldn't be null.
	 * 
	 * @throws DataException
	 */
	public void writeLine(String text) throws DataException;
	
	/**
	 * The last line written.
	 * 
	 * @return Text. May be null if no line has been written yet.
	 */
	public String lastLine();
	
	/**
	 * Reset the internal written to flag.
	 */
	public void resetWrittenTo();
		
	/**
	 * Indicate if an implementation is able to write more than one line.
	 * 
	 * @return true/false.
	 */
	public boolean isMultiLine();	
	
	/**
	 * Free resource, i.e. close streams.
	 * 
	 * @throws DataException
	 */
	public void close() throws DataException;
}
