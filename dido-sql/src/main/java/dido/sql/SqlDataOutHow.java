package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author rob
 */
public class SqlDataOutHow implements DataOutHow<Connection> {

    private final String sql;

    private final int batchSize;

    private final ClassLoader classLoader;

    public static class Options {

        private String sql;

        private int batchSize;

        private ClassLoader classLoader;

        Options(String sql) {
            this.sql = sql;
        }

        Options() {}

        public Options sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Options batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Options classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public DataOut to(Connection connection) throws SQLException, ClassNotFoundException {
            return make_().outTo(connection);
        }

        public DataOutHow<Connection> make() {
            return make_();
        }

        public SqlDataOutHow make_() {
            return new SqlDataOutHow(this);
        }
    }

    public static Options fromSql(String sql) {
        return new Options(sql);
    }

    public static Options with() {
        return new Options();
    }

    private SqlDataOutHow(Options options) {
        this.sql = Objects.requireNonNull(options.sql);
        this.batchSize = options.batchSize;
        this.classLoader = Objects.requireNonNullElse(options.classLoader, getClass().getClassLoader());
    }

    @Override
    public Class<Connection> getOutType() {
        return Connection.class;
    }

    @Override
    public DataOut outTo(Connection connection) throws SQLException, ClassNotFoundException {


        PreparedStatement stmt = connection.prepareStatement(sql);

        ParameterMetaData metaData = stmt.getParameterMetaData();

        int paramCount = metaData.getParameterCount();

        Class<?>[] columnTypes = new Class<?>[paramCount];
        int[] sqlTypes = new int[paramCount];

        for (int i = 1; i <= paramCount; ++i) {

            String className = metaData.getParameterClassName(i);

            Class<?> type = Class.forName(
                    className, true, classLoader);

            columnTypes[i - 1] = type;
            sqlTypes[i - 1] = metaData.getParameterType(i);
        }

        return new DataOut() {

            int count = 0;

            @Override
            public void accept(DidoData data) {

                DataSchema schema = data.getSchema();

                    for (int index = schema.firstIndex(); index != 0; index = schema.nextIndex(index)) {

                        Object item = data.getAt(index);
                        try {
                            if (item == null) {
                                stmt.setNull(index, sqlTypes[index - 1]);
                            } else {
                                stmt.setObject(index, item);
                            }
                        } catch (SQLException e) {
                            throw new DataException("Failed setting column " + index + " with ["
                                    + item + "]", e);
                        }
                    }

                try {
                    if (batchSize > 0) {
                        stmt.addBatch();
                        if (++count % batchSize == 0) {
                            stmt.executeBatch();
                        }
                    } else {
                        stmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    throw new DataException(e);
                }
            }

            @Override
            public void close() throws SQLException {
                stmt.close();
                connection.close();
            }
        };
    }

}
