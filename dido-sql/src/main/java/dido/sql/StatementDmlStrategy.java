package dido.sql;

import dido.data.DataSchema;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Uses the provided Statement as the DML.
 */
public class StatementDmlStrategy implements DmlStrategy {

    private final String statement;

    public StatementDmlStrategy(String statement) {
        this.statement = statement;
    }

    @Override
    public Prepared prepare(Connection connection, DataSchema schema) throws SQLException {

        return new Prepared(connection.prepareStatement(statement),
                schema.getIndices());

    }
}
