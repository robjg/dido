package org.oddjob.dido.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.field.Field;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphDefinitionBuilder;
import org.oddjob.dido.tabular.ColumnHelper;
import org.oddjob.dido.tabular.ColumnIn;

/**
 * A simple implementation of {@link SQLDataIn}.
 * 
 * @author rob
 *
 */
public class SQLDataInImpl implements SQLDataIn {

	private final MorphDefinition morphDefinition;
	
	private final ColumnHelper columnHelper;
	
	private final ResultSet resultSet;
	
	private final Class<?>[] columnTypes; 
	
	public SQLDataInImpl(Connection connection, String sql, 
			ArooaSession session, int fetchSize) throws SQLException {
		
		Statement stmt = connection.createStatement();
		stmt.setFetchSize(fetchSize);
		
		resultSet = stmt.executeQuery(sql);
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		ArooaDescriptor descriptor = session.getArooaDescriptor();

		MorphDefinitionBuilder morphBuilder = new MorphDefinitionBuilder();
		
		int columnCount = metaData.getColumnCount();
		
		columnTypes = new Class<?>[columnCount];
		String[] columnNames = new String[columnCount];
		
		for (int column = 1; column <= columnCount; ++column) {
			
			String columnClassName = metaData.getColumnClassName(column);
			
			Class<?> columnClass = descriptor.getClassResolver().findClass(
					columnClassName);
			
			String columnName = metaData.getColumnName(column);
			
			columnNames[column -1 ] = columnName;
			columnTypes[column - 1] = columnClass;
			
			morphBuilder.add(columnName, columnClass);
		}
		
		morphDefinition = morphBuilder.build();
		columnHelper = new ColumnHelper(columnNames);
	}

	class SQLColumnIn<T> implements ColumnIn<T> {

		private final int columnIndex;

		public SQLColumnIn(int index) {
			this.columnIndex = index;
		}
		
		@Override
		public int getColumnIndex() {
			return columnIndex;
		}
		
		@Override
		public Class<?> getType() {
			return columnTypes[columnIndex - 1];
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T getData() throws DataException {
			try {
				return (T) resultSet.getObject(columnIndex);
			} catch (SQLException e) {
				throw new DataException(e);
			}
		}
	}
	
	@Override
	public MorphDefinition morphOf() {

		return morphDefinition;
	}

	@Override
	public ColumnIn<?> inFor(Field column) {
		
		return new SQLColumnIn<Object>(columnHelper.columnIndexFor(column));
	}
	
	@Override
	public boolean next() throws SQLException {
		return resultSet.next();
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {
		
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		throw new UnsupportedDataInException(getClass(), type);
	}
	
	@Override
	public void close() throws SQLException {		
		Statement stmt = resultSet.getStatement();
		Connection connection = stmt.getConnection();

		stmt.close();
		connection.close();
	}
}
