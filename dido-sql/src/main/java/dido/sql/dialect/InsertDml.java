package dido.sql.dialect;

import dido.data.DataSchema;

/**
 * Create an Insert Statement.
 */
public interface InsertDml {

    String createInsertDml(DataSchema schema, String tableName);
}
