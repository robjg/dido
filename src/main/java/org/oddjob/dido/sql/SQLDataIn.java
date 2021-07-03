package org.oddjob.dido.sql;

import java.sql.SQLException;

import org.oddjob.dido.DataIn;
import org.oddjob.dido.morph.MorphProvider;
import org.oddjob.dido.tabular.TabularDataIn;

public interface SQLDataIn extends DataIn, MorphProvider, TabularDataIn {

	public boolean next() throws SQLException;

	public void close() throws SQLException;
}
