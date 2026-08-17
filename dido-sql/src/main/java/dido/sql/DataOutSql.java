package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.SchemaListener;
import dido.sql.dialect.std.StdInsertDml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author rob
 */
public class DataOutSql implements DataOutHow<Connection> {

    private static final Logger logger = LoggerFactory.getLogger(DataOutSql.class);

    private final DmlStrategy dmlStrategy;

    private final int batchSize;

    private final ClassLoader classLoader;

    private DataOutSql(DmlStrategy dmlStrategy,
                       int batchSize,
                       ClassLoader classLoader) {
        this.dmlStrategy = dmlStrategy;
        this.batchSize = batchSize;
        this.classLoader = Objects.requireNonNullElse(classLoader,
                getClass().getClassLoader());
    }

    public static class Settings {

        private String sql;

        private int batchSize;

        private String table;

        private ClassLoader classLoader;

        private dido.data.schema.SchemaNotifier schemaNotifier;

        Settings() {
        }

        public Settings sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Settings batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Settings table(String table) {
            this.table = table;
            return this;
        }

        public Settings classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Settings schemaNotifier(dido.data.schema.SchemaNotifier schemaNotifier) {
            this.schemaNotifier = schemaNotifier;
            return this;
        }

        public DataOut toConnection(Connection connection) {
            return make()
                    .outTo(connection);
        }

        public DataOutSql make() {

            DmlStrategy dmlStrategy;

            if (sql == null) {
                if (table == null) {
                    throw new IllegalArgumentException("Table name or statement required");
                } else {
                    dmlStrategy = new InsertDmlStrategy(table, new StdInsertDml());
                }
            } else {
                dmlStrategy = new StatementDmlStrategy(sql);
            }

            return new DataOutSql(dmlStrategy,
                    batchSize, classLoader);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<Connection> getOutType() {
        return Connection.class;
    }

    @Override
    public DataOut outTo(Connection outTo) {

        try {
            return outToWithExceptions(outTo);
        } catch (SQLException | ClassNotFoundException e) {
            throw DataException.of(e);
        }
    }

    protected DataOut outToWithExceptions(Connection connection) throws SQLException, ClassNotFoundException {

        return new DataOutImpl(connection);
    }

    class DataOutImpl implements DataOut, SchemaListener {

        private final Connection connection;

        private DmlStrategy.Prepared prepared;

        private int[] sqlTypes;

        private int count = 0;

        DataOutImpl(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void schemaAvailable(DataSchema schema) {

            if (prepared != null) {
                logger.info("Schema already known. Ignoring {}", schema);
            }

            try {
                prepared = dmlStrategy.prepare(connection, schema);

                ParameterMetaData metaData = prepared.statement()
                        .getParameterMetaData();

                int paramCount = metaData.getParameterCount();

                Class<?>[] columnTypes = new Class<?>[paramCount];
                sqlTypes = new int[paramCount];

                for (int i = 1; i <= paramCount; ++i) {

                    String className = metaData.getParameterClassName(i);

                    Class<?> type = Class.forName(
                            className, true, classLoader);

                    columnTypes[i - 1] = type;
                    sqlTypes[i - 1] = metaData.getParameterType(i);
                }

            } catch (SQLException | ClassNotFoundException e) {
                throw new DataException(e);
            }

        }

        @Override
        public void accept(DidoData data) {

            if (prepared == null) {
                schemaAvailable(data.getSchema());
            }

            PreparedStatement stmt = prepared.statement();

            for (int i = 0; i < prepared.indices().length; ++i) {

                int index = prepared.indices()[i];

                Object item = data.getAt(index);
                try {
                    if (item == null) {
                        stmt.setNull(index, sqlTypes[index - 1]);
                    } else {
                        stmt.setObject(index, item);
                    }
                } catch (SQLException e) {
                    throw DataException.of("Failed setting column " + index + " with ["
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
                throw DataException.of(e);
            }
        }

        @Override
        public void close() {
            try (connection) {
                if (prepared != null) {
                    prepared.statement().close();
                }
            } catch (SQLException e) {
                throw new DataException(e);
            }
        }
    }

}
