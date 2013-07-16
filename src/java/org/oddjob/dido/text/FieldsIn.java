package org.oddjob.dido.text;

import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnIn;
import org.oddjob.dido.column.ColumnarDataIn;

public interface FieldsIn extends ColumnarDataIn {

	@Override
	public ColumnIn<String> columnInFor(Column column);
	
}
