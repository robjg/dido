package dido.sql;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.how.util.ClassUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SchemaUtils {

    public static DataSchema<String> schemaFrom(ResultSetMetaData metaData, ClassLoader classLoader)
            throws SQLException, ClassNotFoundException {

        int columnCount = metaData.getColumnCount();

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (int column = 1; column <= columnCount; ++column) {

            String columnClassName = metaData.getColumnClassName(column);

            Class<?> columnClass = ClassUtils.classFor(
                    columnClassName, classLoader);

            String columnName = metaData.getColumnName(column);

            schemaBuilder.addIndexedField(column, columnName, columnClass);
        }

        return schemaBuilder.build();
    }

}
