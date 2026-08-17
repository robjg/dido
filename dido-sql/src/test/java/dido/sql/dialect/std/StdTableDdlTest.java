package dido.sql.dialect.std;

import dido.data.DataSchema;
import dido.sql.dialect.hsql.HsqlSqlTypes;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class StdTableDdlTest {

    @Test
    void createTableDdl() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        StdTableDdl test = new StdTableDdl(new HsqlSqlTypes());

        String ddl = test.createTableDdl(schema, "Fruit");

        assertThat(ddl, containsString(
                "create table Fruit (Fruit VARCHAR(128), Qty INTEGER, Price DOUBLE)"));
    }

}