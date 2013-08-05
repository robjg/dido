package org.oddjob.dido.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.UnsupportedDataOutException;

public class ConnectionDataImpl 
implements ConnectionDataIn, ConnectionDataOut, ArooaSessionAware {

	private ArooaValue connection;
	
	private DataSource dataSource;
	
	private ArooaConverter converter;

	@Override
	public void setArooaSession(ArooaSession session) {
		this.converter = session.getTools().getArooaConverter();
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedDataInException(getClass(), type);
	}

	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws DataException {
		
		if (type.isInstance(this)) {
			return type.cast(this);
		}

		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public boolean isWrittenTo() {
		return false;
	}
	
	@Override
	public Connection connection() throws DataException {
		if (connection == null) {
			if (dataSource == null) {
				throw new DataException("No Connection or Data Source.");
			}
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {
				throw new DataException(e);
			}
		}
		
		try {
			return converter.convert(connection, Connection.class);
		} 
		catch (NoConversionAvailableException e) {
			throw new DataException(e);
		} 
		catch (ConversionFailedException e) {
			throw new DataException(e);
		}
	}

	public ArooaValue getConnection() {
		return connection;
	}
	
	public void setConnection(ArooaValue connection) {
		this.connection = connection;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
