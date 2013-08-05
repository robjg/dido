package org.oddjob.dido.poi;

import org.oddjob.dido.DataOut;

/**
 * Provide a {@link DataOut} for writing a set of rows (i.e. a table) to
 * a sheet of a spreadsheet.
 * 
 * @author rob
 *
 */
public interface RowsOut extends DataOut {

	/**
	 * The 1 based index of the last row written to. If no row has been
	 * written yet then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	public int getLastRow();
	
	/**
	 * The 1 based index of the last column in the table of rows. If no
	 * column has been requested then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	public int getLastColumn();
	
	/**
	 * Create a headings row. The row will be populated by the columns.
	 * 
	 * @param headingStyle The name of the heading style to use.
	 */
	public void headerRow(String headingStyle);
	
	/**
	 * Create the next row for writing to.
	 */
	public void nextRow();

	/**
	 * Add auto filter to all the columns.
	 */
	public void autoFilter();
	
	/**
	 * Auto width all the columns.
	 */
	public void autoWidth();
	
}
