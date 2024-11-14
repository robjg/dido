package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author rob
 */
public class DataInSql implements DataInHow<Connection> {

    private final String sql;

    private final int batchSize;

    private final ClassLoader classLoader;

    private final DataSchema schema;

    public static class Settings {

        private String sql;

        private int batchSize;

        private DataSchema schema;

        private ClassLoader classLoader;

        public Settings sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Settings batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Settings classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public DataIn fromConnection(Connection connection) {
            return make().inFrom(connection);
        }

        public DataInSql make() {
            return new DataInSql(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    private DataInSql(Settings settings) {
        this.sql = Objects.requireNonNull(settings.sql);
        this.batchSize = settings.batchSize;
        this.schema = settings.schema;
        this.classLoader = Objects.requireNonNullElse(settings.classLoader, getClass().getClassLoader());
    }

    @Override
    public Class<Connection> getInType() {
        return Connection.class;
    }

    @Override
    public DataIn inFrom(Connection connection) {

        try {
            Statement stmt = connection.createStatement();
            stmt.setFetchSize(batchSize);

            ResultSet resultSet = stmt.executeQuery(sql);

            DidoData wrapper = ResultSetWrapper.from(resultSet, schema, null);

            return new DataIn() {

                @Override
                public Iterator<DidoData> iterator() {
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            try {
                                return resultSet.next();
                            } catch (SQLException e) {
                                throw DataException.of(e);
                            }
                        }

                        @Override
                        public DidoData next() {
                            return wrapper;
                        }
                    };
                }

                @Override
                public void close() {

                    try (connection) {
                        stmt.close();
                    } catch (SQLException e) {
                        throw DataException.of(e);
                    }
                }
            };
        } catch (SQLException | ClassNotFoundException e) {
            throw DataException.of(e);
        }
    }
}
