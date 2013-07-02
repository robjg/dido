package org.oddjob.dido.column;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;

public interface ColumnarDataOut extends Columnar, DataOut {

	public <T> void setColumnData(int columnIndex, T data)
	throws DataException;
}
