package org.oddjob.dido.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedeDataInException;
import org.oddjob.dido.column.ColumnMetaData;
import org.oddjob.dido.morph.MorphDefinition;
import org.oddjob.dido.morph.MorphDefinitionBuilder;

public class SQLDataInImpl implements SQLDataIn {

	private final MorphDefinition morphDefinition;
	
	private final Map<String, Integer> columnPositions = 
			new HashMap<String, Integer>();
	
	private final ResultSet resultSet;
	
	private final Class<?>[] columnTypes; 
	
	private int lastColumnIndex;
	
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
		
		for (int column = 1; column <= columnCount; ++column) {
			
			String columnClassName = metaData.getColumnClassName(column);
			
			Class<?> columnClass = descriptor.getClassResolver().findClass(
					columnClassName);
			
			String columnName = metaData.getColumnName(column);
			
			columnPositions.put(columnName, new Integer(column));
			
			morphBuilder.add(columnName, columnClass);
		}
		
		morphDefinition = morphBuilder.build();
	}

	@Override
	public MorphDefinition morphOf() {

		return morphDefinition;
	}
		
	@Override
	public int columnIndexFor(String columnName, int columnIndex) {
		if (columnIndex > 0) {
			lastColumnIndex = columnIndex;
			return columnIndex;
		}
		else {
			++lastColumnIndex;
			return lastColumnIndex;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getColumnData(int column) throws DataException {
		try {
			return (T) resultSet.getObject(column);
		} catch (SQLException e) {
			throw new DataException(e);
		}
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

		throw new UnsupportedeDataInException(getClass(), type);
	}
	
	@Override
	public void close() throws SQLException {		
		Statement stmt = resultSet.getStatement();
		Connection connection = stmt.getConnection();

		stmt.close();
		connection.close();
	}
}
