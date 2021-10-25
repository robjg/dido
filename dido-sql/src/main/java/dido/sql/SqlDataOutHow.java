package dido.sql;

import dido.data.DataSchema;
import dido.data.GenericData;
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
public class SqlDataOutHow implements DataOutHow<String, Connection> {

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

        public DataOutHow<String, Connection> make() {
            return new SqlDataOutHow(this);
        }
    }

    public static Options fromSql(String sql) {
        return new Options(sql);
    }

    private SqlDataOutHow(Options options) {
        this.sql = Objects.requireNonNull(options.sql);
        this.batchSize = options.batchSize;
        this.classLoader = options.classLoader;
    }

    @Override
    public Class<Connection> getOutType() {
        return Connection.class;
    }

    @Override
    public DataOut<String> outTo(Connection connection) throws Exception {


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

        return new DataOut<>() {

            int count = 0;

            @Override
            public void accept(GenericData<String> data) {

                DataSchema<String> schema = data.getSchema();

                try {
                    for (int index = schema.firstIndex(); index != 0; index = schema.nextIndex(index)) {

                        Object item = data.getAt(index);
                        if (item == null) {
                            stmt.setNull(index, sqlTypes[index - 1]);
                        } else {
                            stmt.setObject(index, item);
                        }
                    }

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
