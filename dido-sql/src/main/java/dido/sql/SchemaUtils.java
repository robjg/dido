package dido.sql;

import dido.data.DataSchema;
import dido.data.SchemaFactory;
import dido.data.SchemaField;
import dido.data.schema.DataSchemaFactory;
import dido.data.util.ClassUtils;
import dido.how.DataException;
import dido.sql.dialect.SqlTypes;
import dido.sql.dialect.std.StdSqlTypes;

import java.sql.*;

public class SchemaUtils {

    private final ClassLoader classLoader;

    protected SchemaUtils(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        this.classLoader = classLoader;
    }

    public static DataSchema schemaFrom(ResultSetMetaData metaData)
            throws SQLException, ClassNotFoundException {
        return schemaFrom(metaData, null);
    }

    public static DataSchema schemaFrom(ResultSetMetaData metaData, ClassLoader classLoader)
            throws SQLException, ClassNotFoundException {

        SchemaUtils schemaUtils = new SchemaUtils(classLoader);

        int columnCount = metaData.getColumnCount();

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();

        for (int column = 1; column <= columnCount; ++column) {

            schemaFactory.addSchemaField(schemaUtils.getSchemaField(column, metaData));
        }

        return schemaFactory.toSchema();
    }

    public static SchemaUtils forClassLoader(ClassLoader classLoader) {
        return new SchemaUtils(classLoader);
    }

    public SchemaField getSchemaField(int column, ResultSetMetaData metaData) throws SQLException, ClassNotFoundException {

        String columnClassName = metaData.getColumnClassName(column);

        Class<?> columnClass = ClassUtils.classFor(
                columnClassName, classLoader);

        String columnName = metaData.getColumnLabel(column);
        // JDBC spec says this shouldn't happen but seems to with HSQL.
        if (columnName == null) {
            columnName = metaData.getColumnName(column);
        }

        return SchemaField.of(0, columnName, columnClass);
    }

    public Class<?> getColumnType(int column, ResultSetMetaData metaData) throws SQLException, ClassNotFoundException {

        String columnClassName = metaData.getColumnClassName(column);

        return ClassUtils.classFor(
                columnClassName, classLoader);

    }

    public DataSchema schemaForTable(Connection connection,
                                     String tableName) {

        SqlTypes sqlTypes = new StdSqlTypes();

        SchemaFactory schemaFactory = SchemaFactory.newInstance();

        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            try (ResultSet columns = databaseMetaData.getColumns(
                    null, null, tableName, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    int datatype = columns.getInt("DATA_TYPE");
                    schemaFactory.addSchemaField(
                            SchemaField.of(0, columnName,
                                    sqlTypes.getJavaType(datatype)));
                }
            }
        } catch (SQLException e) {
            throw new DataException(e);
        }

        return schemaFactory.toSchema();
    }
}
