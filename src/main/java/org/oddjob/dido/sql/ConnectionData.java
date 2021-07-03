package org.oddjob.dido.sql;

import java.sql.Connection;

import org.oddjob.dido.DataException;

public interface ConnectionData {

	public Connection connection() throws DataException;
}
