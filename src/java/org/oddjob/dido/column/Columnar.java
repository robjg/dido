package org.oddjob.dido.column;

/**
 * Base for Data that is in columns.
 * 
 * @author rob
 *
 */
public interface Columnar {

	/**
	 * Get the column index to use. This will either be base on the 
	 * heading provided or on the column index provided. 
	 * <p>
	 * Text based implementations may then use the heading for writing a 
	 * heading line.
	 * 
	 * @param heading The heading, can be null.
	 * @param column The column. If 0 a column is to be assigned.
	 * 
	 * @return The column assigned.
	 */
	public int columnIndexFor(String columnName, int columnIndex);
	
	/**
	 * Get the column meta data.
	 * 
	 * @param columnIndex The column index. 1 based.
	 * 
	 * @return The meta data. Will not be null.
	 */
	public ColumnMetaData getColumnMetaData(int columnIndex);
}
