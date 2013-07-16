package org.oddjob.dido.column;

import org.oddjob.dido.DataException;

public interface ColumnIn<T> extends ColumnData<T> {

	public T getColumnData()
	throws DataException;
}
