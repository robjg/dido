package org.oddjob.dido.field;

import org.oddjob.dido.DataException;

public interface FieldOut<T> extends FieldData {

	public void setData(T data)
	throws DataException;
}
