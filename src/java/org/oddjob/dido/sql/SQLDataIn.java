package org.oddjob.dido.sql;

import java.sql.SQLException;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.column.ColumnarDataIn;
import org.oddjob.dido.morph.MorphProvider;

public interface SQLDataIn extends DataIn, MorphProvider, ColumnarDataIn {

	public boolean next() throws SQLException;

	public void close() throws SQLException;
}
