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
import org.oddjob.dido.field.Field;
import org.oddjob.dido.tabular.ColumnHelper;
import org.oddjob.dido.tabular.ColumnOut;

/**
 * A simple implementation of {@link SQLDataOut}.
 * 
 * @author rob
 *
 */
public class SQLDataOutImpl implements SQLDataOut {

	private final ColumnHelper columnHelper  = new ColumnHelper();
	
	private final PreparedStatement stmt;

	private final Class<?>[] columnTypes; 
	
	private final int[] sqlTypes;
	
	private boolean writtenTo;

	/**
	 * Create a new instance.
	 * 
	 * @param connection The DB connection. This class will close it.
	 * @param sql The SQL DML statement.
	 * @param session The session. Used for class resolution.
	 * 
	 * @throws SQLException
	 */
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
		
		@Override
		public Class<?> getType() {
			if (columnIndex == 0) {
				return null;
			}
			else {
				return columnTypes[columnIndex - 1];
			}
		}
		
		@Override
		public void setData(T data) throws DataException {
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
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ": index=" + columnIndex;
		}
	}
	
	@Override
	public ColumnOut<?> outFor(Field column) {

		return new SQLColumnOut<Object>(
				columnHelper.columnIndexFor(column));
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
