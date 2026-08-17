package dido.sql.dialect.std;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.sql.dialect.InsertDml;

/**
 * Create a standard Insert statement.
 */
public class StdInsertDml implements InsertDml {

    @Override
    public String createInsertDml(DataSchema schema, String tableName) {
        StringBuilder insert = new StringBuilder();

        insert.append("insert into ");
        insert.append(tableName);
        insert.append(" (");

        int lastIndex = schema.lastIndex();

        for (SchemaField schemaField : schema.getSchemaFields()) {

            insert.append(schemaField.getName());
            if (schemaField.getIndex() != lastIndex) {
                insert.append(", ");
            }
        }

        insert.append(") values (");

        for (SchemaField schemaField : schema.getSchemaFields()) {

            insert.append("?");
            if (schemaField.getIndex() != lastIndex) {
                insert.append(", ");
            }
        }

        insert.append(")");

        return insert.toString();
    }
}
