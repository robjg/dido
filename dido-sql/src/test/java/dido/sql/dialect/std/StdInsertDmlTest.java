package dido.sql.dialect.std;

import dido.data.DataSchema;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class StdInsertDmlTest {

    @Test
    void insert() {

        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        StdInsertDml test = new StdInsertDml();

        String ddl = test.createInsertDml(schema, "Fruit");

        assertThat(ddl, containsString(
                "insert into Fruit (Fruit, Qty, Price) values (?, ?, ?)"));

    }

}