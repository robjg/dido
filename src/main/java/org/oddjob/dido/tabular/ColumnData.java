package org.oddjob.dido.tabular;

import org.oddjob.dido.field.FieldData;

/**
 * An abstraction for data that is tabular.
 * 
 * @see ColumnOut
 * @see ColumnIn
 * 
 * @author rob
 *
 */
public interface ColumnData extends FieldData {

	/**
	 * The column index. This is a 1 based index of the column number in
	 * the table.
	 * 
	 * @return The 1 based index. 0 if the index is unknown.
	 */
	public int getColumnIndex();
	
}
