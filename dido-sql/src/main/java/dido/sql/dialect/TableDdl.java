package dido.sql.dialect;

import dido.data.DataSchema;

/**
 * Something that can construct the DDL required to create a table
 * using a Schema and a Table Name.
 */
public interface TableDdl {

    String createTableDdl(DataSchema schema, String tableName);

}
