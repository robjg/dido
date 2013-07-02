package org.oddjob.dido.sql;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedeDataOutException;
import org.oddjob.dido.column.ColumnMetaData;

public class SQLDataOutImpl implements SQLDataOut {

	private final PreparedStatement stmt;

	private final Class<?>[] columnTypes; 
	
	private final int[] sqlTypes;
	
	private boolean writtenTo;

	private int lastColumnIndex;
	
	public SQLDataOutImpl(Connection connection, String sql, 
			ArooaSession session) 
	throws SQLException {
		
		stmt = connection.prepareStatement(sql);

		ParameterMetaData metaData = stmt.getParameterMetaData();
		
		ArooaDescriptor descriptor = session.getArooaDescriptor();
		
		int paramCount = metaData.getParameterCount();
		
		columnTypes = new Class<?>[paramCount];
		sqlTypes = new int[paramCount];
		
		for (int i = 1; i <= paramCount; ++i) {
			
			String className = metaData.getParameterClassName(i);
			
			Class<?> type = descriptor.getClassResolver().findClass(
					className);
			
			columnTypes[i - 1] = type;
			sqlTypes[i - 1] = metaData.getParameterType(i);
		}
	}

	@Override
	public ColumnMetaData getColumnMetaData(final int columnIndex) {
		return new ColumnMetaData() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <T> Class<T> getColumnType() {
				return (Class<T>) columnTypes[columnIndex - 1];
			}
		};
	}

	@Override
	public int columnIndexFor(String columnName, int column) {
		if (column > 0) {
			lastColumnIndex = column;
			return column;
		}
		else {
			++lastColumnIndex;
			return lastColumnIndex;
		}
	}
	
	@Override
	public <T> void setColumnData(int columnIndex, T data) 
	throws DataException {
		try {
			if (data == null) {
				stmt.setNull(columnIndex, sqlTypes[columnIndex -1]);
			}
			stmt.setObject(columnIndex, data);
		} 
		catch (SQLException e) {
			throw new DataException(e);
		}
		writtenTo = true;
	}
	
	@Override
	public void addBatch() throws SQLException {
		stmt.addBatch();
	}
	
	@Override
	public void execute() throws SQLException {
		stmt.executeBatch();
	}
	
	@Override
	public boolean isWrittenTo() {
		return writtenTo;
	}
	
	@Override
	public void resetWrittenTo() {
		writtenTo = false;
	}
		
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
			throws UnsupportedeDataOutException {
		
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}
		
		throw new UnsupportedeDataOutException(getClass(), type);
	}
	
	@Override
	public void close() throws SQLException {
		Connection connection = stmt.getConnection();

		stmt.close();
		connection.close();
	}	
}
