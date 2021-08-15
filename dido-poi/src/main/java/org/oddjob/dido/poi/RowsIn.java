package org.oddjob.dido.poi;

import org.oddjob.dido.DataIn;

public interface RowsIn extends DataIn {

	/**
	 * The 1 based index of the last row read. If no row has been
	 * read yet then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	public int getLastRow();
	
	/**
	 * 
	 * @return
	 */
	public int getLastColumn();
	
	/**
	 * Read a header row.
	 * 
	 * @return The heading, or null if the header row doesn't exist.
	 */
	public String[] headerRow();
	
	/**
	 * Advance the current row. 
	 * 
	 * @return True if there is another row , false if there
	 * isn't.
	 */
	public boolean nextRow();
}
