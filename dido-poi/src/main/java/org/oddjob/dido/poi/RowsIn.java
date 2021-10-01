package org.oddjob.dido.poi;

public interface RowsIn {

	/**
	 * The 1 based index of the last row read. If no row has been
	 * read yet then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	int getLastRow();

	/**
	 * Read a header row.
	 * 
	 * @return The heading, or null if the header row doesn't exist.
	 */
	String[] headerRow();
	
	/**
	 * Advance the current row and return it.
	 * 
	 * @return Return another row if there is one, null if there
	 * isn't.
	 */
	RowIn nextRow();

	RowIn peekRow();
}
