package dido.sql.dialect.std;

import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StdSqlTypesTest {

    @Test
    void sqlTypes() {

        StdSqlTypes test = new StdSqlTypes();

        assertThat(test.getSqlType(String.class), is(Types.VARCHAR));
        assertThat(test.getSqlType(Integer.class), is(Types.INTEGER));
    }
}