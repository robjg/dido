package org.oddjob.dido.field;

import org.oddjob.dido.DataException;

public interface FieldIn<T> extends FieldData {

	public T getData()
	throws DataException;
}
