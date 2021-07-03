package org.oddjob.dido.sql;

import java.sql.SQLException;

import org.oddjob.dido.DataOut;
import org.oddjob.dido.tabular.TabularDataOut;

public interface SQLDataOut extends DataOut, TabularDataOut {

	public void resetWrittenTo();
	
	public void addBatch() throws SQLException;
	
	public void execute() throws SQLException;
	
	public void close() throws SQLException;
	
}
