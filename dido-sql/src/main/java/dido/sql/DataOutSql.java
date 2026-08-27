package dido.sql;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.ReadSchema;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.RefinableOutHow;
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
public class DataOutSql implements RefinableOutHow<Connection> {

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

        return new UnknownDataOut(connection);
    }

    public class KnownOutHow implements DataOutHow<Connection> {

        private final DataSchema schema;

        KnownOutHow(DataSchema schema) {
            this.schema = schema;
        }

        @Override
        public Class<Connection> getOutType() {
            return DataOutSql.this.getOutType();
        }

        @Override
        public DataOut outTo(Connection outTo) {
            return new KnownDataOut(schema, outTo);
        }

        @Override
        public String toString() {
            return DataOutSql.this.toString();
        }
    }

    @Override
    public DataOutHow<Connection> forSchema(DataSchema schema) {

        logger.info("Schema available {}", schema);

        return new KnownOutHow(schema);
    }

    class KnownDataOut implements DataOut {

        private final Connection connection;

        private final DmlStrategy.Prepared prepared;

        private final int[] sqlTypes;

        private final FieldGetter[] getters;

        private int count = 0;

        KnownDataOut(DataSchema schema,
                     Connection connection) {

            this.connection = connection;

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

                ReadSchema readSchema = ReadSchema.from(schema);

                getters = new FieldGetter[prepared.indices().length];
                for (int i = 0; i < prepared.indices().length; ++i) {

                    int index = prepared.indices()[i];

                    getters[i] = readSchema.getFieldGetterAt(index);
                }

            } catch (SQLException | ClassNotFoundException e) {
                throw new DataException(e);
            }

        }

        @Override
        public void accept(DidoData data) {

            PreparedStatement stmt = prepared.statement();

            for (int i = 0; i < getters.length; ++i) {

                Object item = getters[i].get(data);
                try {
                    if (item == null) {
                        stmt.setNull(i + 1, sqlTypes[i]);
                    } else {
                        stmt.setObject(i + 1, item);
                    }
                } catch (SQLException e) {
                    throw DataException.of("Failed setting column " + (i + 1)  + " with ["
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

    class UnknownDataOut implements DataOut {

        private final Connection connection;

        private DataOut knownDataOut;

        UnknownDataOut(Connection connection) {
            this.connection = connection;
        }


        @Override
        public void accept(DidoData didoData) {
            if (knownDataOut == null) {
                knownDataOut = forSchema(didoData.getSchema())
                        .outTo(connection);
            }
            knownDataOut.accept(didoData);
        }

        @Override
        public void close() {
            if (knownDataOut != null) {
                knownDataOut.close();
            }
        }

    }

}
