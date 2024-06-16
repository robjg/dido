package dido.sql;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.how.util.ClassUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SchemaUtils {

    public static DataSchema schemaFrom(ResultSetMetaData metaData)
            throws SQLException, ClassNotFoundException {
        return schemaFrom(metaData, null);
    }

    public static DataSchema schemaFrom(ResultSetMetaData metaData, ClassLoader classLoader)
            throws SQLException, ClassNotFoundException {

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        int columnCount = metaData.getColumnCount();

        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        for (int column = 1; column <= columnCount; ++column) {

            String columnClassName = metaData.getColumnClassName(column);

            Class<?> columnClass = ClassUtils.classFor(
                    columnClassName, classLoader);

            String columnName = metaData.getColumnName(column);

            schemaBuilder.addFieldAt(column, columnName, columnClass);
        }

        return schemaBuilder.build();
    }
}
