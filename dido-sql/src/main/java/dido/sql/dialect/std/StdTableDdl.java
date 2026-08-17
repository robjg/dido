package dido.sql.dialect.std;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.sql.dialect.SqlTypes;
import dido.sql.dialect.TableDdl;

/**
 * Creates the DDL for creating a table. Work in Progress.
 */
public class StdTableDdl implements TableDdl {

    public final SqlTypes sqlTypes;

    public StdTableDdl(SqlTypes sqlTypes) {
        this.sqlTypes = sqlTypes;
    }

    @Override
    public String createTableDdl(DataSchema schema, String tableName) {

        StringBuilder ddl = new StringBuilder();

        ddl.append("create table ");
        ddl.append(tableName);
        ddl.append(" (");

        int lastIndex = schema.lastIndex();

        for (SchemaField schemaField : schema.getSchemaFields()) {

            ddl.append(schemaField.getName());
            ddl.append(" ");
            ddl.append(sqlTypes.getSqlTypeName(
                    sqlTypes.getSqlType(schemaField.getType())));
            if (schemaField.getIndex() != lastIndex) {
                ddl.append(", ");
            }
        }

        ddl.append(")");

        return ddl.toString();
    }
}
