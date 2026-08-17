package dido.sql;

import dido.data.DataSchema;
import dido.sql.dialect.InsertDml;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Creates the Insert DML from a table name using the schema for the column
 * names.
 */
public class InsertDmlStrategy implements DmlStrategy {

    private final String table;

    private final InsertDml insertDml;

    public InsertDmlStrategy(String table, InsertDml insertDml) {
        this.table = table;
        this.insertDml = insertDml;
    }

    @Override
    public Prepared prepare(Connection connection, DataSchema schema) throws SQLException {

        String dml = insertDml.createInsertDml(schema, table);

        return new StatementDmlStrategy(dml).prepare(connection, schema);
    }
}
