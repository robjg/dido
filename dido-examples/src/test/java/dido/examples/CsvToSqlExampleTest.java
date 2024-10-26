package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.sql.SqlDataInHow;
import dido.sql.SqlDataOutHow;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CsvToSqlExampleTest {

    @Test
    void example() throws Exception {

        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");

        String create = "drop table PEOPLE if exists;\n" +
                "\n" +
                "create table PEOPLE(" +
                "\"Index\" varchar(50), " +
                "\"User Id\" varchar(50), " +
                "\"First Name\" varchar(50), " +
                "\"Last Name\" varchar(50), " +
                "\"Sex\" varchar(10), " +
                "\"Email\" varchar(50), " +
                "\"Phone\" varchar(50), " +
                "\"Date of birth\" varchar(20)," +
                "\"Job Title\" varchar(100)" +
                ")";

        connection.createStatement().execute(create);

        // #snippet1{
        try (DataIn in = DataInCsv.with()
                .header(true)
                .fromInputStream(getClass().getResourceAsStream("/examples/people-100.csv"));
             DataOut out = SqlDataOutHow.with()
                     .sql("insert into PEOPLE " +
                             "(\"Index\",\"User Id\",\"First Name\",\"Last Name\",\"Sex\",\"Email\",\"Phone\",\"Date of birth\",\"Job Title\")" +
                             " values (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                     .to(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""))) {

            in.stream().forEach(out);
        }
        // }#snippet1

        StringBuilder stringBuilder = new StringBuilder();

        try (DataIn in = SqlDataInHow.fromSql("select * from people")
                .make()
                .inFrom(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""));
             DataOut out = DataOutCsv.with()
                     .header(true)
                     .toAppendable(stringBuilder)) {

            for (DidoData data : in) {
                out.accept(data);
            }
        }

        connection.close();

        String expected = new String(new BufferedInputStream(
                Objects.requireNonNull(getClass().getResourceAsStream("/examples/people-100.csv")))
                .readAllBytes());

        assertThat(stringBuilder.toString(), is(expected));
    }
}
