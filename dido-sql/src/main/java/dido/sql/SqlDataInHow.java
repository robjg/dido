package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.sql.*;
import java.util.Objects;

/**
 *
 * @author rob
 *
 */
public class SqlDataInHow implements DataInHow<Connection> {

	private final String sql;

	private final int batchSize;

	private final ClassLoader classLoader;

	public static class Options {

		private final String sql;

		private int batchSize;

		private ClassLoader classLoader;

		public Options(String sql) {
			this.sql = sql;
		}

		public Options batchSize(int batchSize) {
			this.batchSize = batchSize;
			return this;
		}

		public Options classLoader(ClassLoader classLoader) {
			this.classLoader = classLoader;
			return this;
		}

		public DataInHow<Connection> make() {
			return new SqlDataInHow(this);
		}
	}

	public static Options fromSql(String sql) {
		return new Options(sql);
	}

	private SqlDataInHow(Options options) {
		this.sql = Objects.requireNonNull(options.sql);
		this.batchSize = options.batchSize;
		this.classLoader = Objects.requireNonNullElse(options.classLoader, getClass().getClassLoader());
	}

	@Override
	public Class<Connection> getInType() {
		return Connection.class;
	}

	@Override
	public DataIn inFrom(Connection connection) throws Exception {

		Statement stmt = connection.createStatement();
		stmt.setFetchSize(batchSize);

		ResultSet resultSet = stmt.executeQuery(sql);

		ResultSetMetaData metaData = resultSet.getMetaData();

		DataSchema<String> schema = SchemaUtils.schemaFrom(metaData, classLoader);

		DidoData wrapper = ResultSetWrapper.from(resultSet, schema);

		return new DataIn() {
			@Override
			public DidoData get() {
				try {
					if (resultSet.next()) {
						return wrapper;
					} else {
						return null;
					}
				} catch (SQLException e) {
					throw new DataException(e);
				}
			}

			@Override
			public void close() throws Exception {

				stmt.close();
				connection.close();
			}
		};
	}
}
