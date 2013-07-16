package org.oddjob.dido.column;

import org.oddjob.dido.DataException;

public interface ColumnOut<T> extends ColumnData<T> {

	public void setColumnData(T data)
	throws DataException;
}
