package org.oddjob.dido.tabular;

import org.oddjob.dido.field.Field;
import org.oddjob.dido.field.FieldDataOut;

public interface TabularDataOut extends FieldDataOut {

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
	public ColumnOut<?> outFor(Field column);
}
