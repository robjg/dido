package org.oddjob.dido.sql;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.column.Column;
import org.oddjob.dido.column.ColumnOut;

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

	
	class SQLColumnOut<T> implements ColumnOut<T> {
		
		private final int columnIndex;
		
		public SQLColumnOut(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<T> getColumnType() {
			if (columnIndex == 0) {
				return (Class<T>) Void.TYPE;
			}
			else {
				return (Class<T>) columnTypes[columnIndex - 1];
			}
		}
		
		@Override
		public void setColumnData(T data) throws DataException {
			if (columnIndex != 0) {
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
		}
	}
	
	@Override
	public ColumnOut<?> columnOutFor(Column column) {

		if (column.getColumnIndex() > 0) {
			lastColumnIndex = column.getColumnIndex();
		}
		else {
			++lastColumnIndex;
		}
		return new SQLColumnOut<Object>(lastColumnIndex);
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
			throws UnsupportedDataOutException {
		
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}
		
		throw new UnsupportedDataOutException(getClass(), type);
	}
	
	@Override
	public void close() throws SQLException {
		Connection connection = stmt.getConnection();

		stmt.close();
		connection.close();
	}	
}
