package dido.sql;

import dido.data.DataSchema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Something that will create DML for {@link DataOutSql}.
 */
public interface DmlStrategy {

    Prepared prepare(Connection connection, DataSchema schema) throws SQLException;

    /**
     * Wrapper around the Prepared Statement.
     *
     * @param statement The statement.
     * @param indices The indices mapping.
     */
    record Prepared(PreparedStatement statement, int[] indices) {
    }




}
