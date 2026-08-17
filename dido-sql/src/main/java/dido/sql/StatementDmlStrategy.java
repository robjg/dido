package dido.sql;

import dido.data.DataSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Uses the provided Statement as the DML.
 */
public class StatementDmlStrategy implements DmlStrategy {

    private static final Logger logger = LoggerFactory.getLogger(StatementDmlStrategy.class);

    private final String statement;

    public StatementDmlStrategy(String statement) {
        this.statement = statement;
    }

    @Override
    public Prepared prepare(Connection connection, DataSchema schema) throws SQLException {

        logger.info("Preparing statement: {}", statement);

        return new Prepared(connection.prepareStatement(statement),
                schema.getIndices());

    }
}
