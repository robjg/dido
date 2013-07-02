package org.oddjob.dido.column;

import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;

public interface ColumnarDataIn extends Columnar, DataIn {

	public <T> T getColumnData(int columnIndex)
	throws DataException;
}
