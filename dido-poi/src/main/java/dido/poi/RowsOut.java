package dido.poi;


/**
 * Something for writing a set of rows (i.e. a table) to
 * a sheet of a spreadsheet.
 * 
 * @author rob
 *
 */
public interface RowsOut {

	/**
	 * The 1 based index of the last row written to. If no row has been
	 * written yet then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	int getLastRow();
	
	/**
	 * The 1 based index of the last column in the table of rows. If no
	 * column has been requested then this will be 0.
	 * 
	 * @return The index. 0 or above.
	 */
	int getLastColumn();

	/**
	 * Create a headings row. The row will be populated by the columns.
	 *
	 * @param headingStyle The name of the heading style to use.
	 */
	HeaderRowOut headerRow(String headingStyle);

	/**
	 * Create the next row for writing to.
	 */
	void nextRow();

	RowOut getRowOut();

	/**
	 * Add auto filter to all the columns.
	 */
	void autoFilter();
	
	/**
	 * Auto width all the columns.
	 */
	void autoWidth();
}
