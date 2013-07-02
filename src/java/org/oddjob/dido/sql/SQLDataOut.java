package org.oddjob.dido.sql;

import java.sql.SQLException;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.column.ColumnarDataOut;

public interface SQLDataOut extends DataOut, ColumnarDataOut {

	public void resetWrittenTo();
	
	public void addBatch() throws SQLException;
	
	public void execute() throws SQLException;
	
	public void close() throws SQLException;
	
}
