package org.oddjob.dido.text;

import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnOut;
import org.oddjob.dido.column.ColumnarDataOut;

public interface FieldsOut extends ColumnarDataOut {

	/**
	 * Write a heading. The heading will only be written
	 * if headings are required, otherwise this method is
	 * only for assigning columns. If the column is
	 * provided then that column is assigned and the
	 * return value of this method will be that column.
	 * 
	 * @param heading The heading, can be null.
	 * @param column The column. If 0 a column will be assigned.
	 * 
	 * @return The column assigned.
	 */
	@Override
	public ColumnOut<String> columnOutFor(Column column);
}
