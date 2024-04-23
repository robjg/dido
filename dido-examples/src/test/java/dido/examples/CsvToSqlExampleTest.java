package dido.examples;

import dido.csv.CsvDataInHow;
import dido.csv.CsvDataOutHow;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.sql.SqlDataInHow;
import dido.sql.SqlDataOutHow;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.io.BufferType;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

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
        try (DataIn<String> in = CsvDataInHow.withOptions()
                .withHeader(true)
                .make()
                .inFrom(getClass().getResourceAsStream("/examples/people-100.csv"));

             DataOut<String> out = SqlDataOutHow.fromSql(
                             "insert into PEOPLE " +
                                     "(\"Index\",\"User Id\",\"First Name\",\"Last Name\",\"Sex\",\"Email\",\"Phone\",\"Date of birth\",\"Job Title\")" +
                                     " values (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                     .make()
                     .outTo(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""))) {

            for (GenericData<String> data : in) {
                out.accept(data);
            }
        }
        // }#snippet1

        BufferType bufferType = new BufferType();
        bufferType.configured();

        try (DataIn<String> in = SqlDataInHow.fromSql("select * from people")
                .make()
                .inFrom(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""));
             DataOut<String> out = CsvDataOutHow.withOptions()
                     .withHeader(true)
                     .make()
                     .outTo(bufferType.toOutputStream())) {

            for (GenericData<String> data : in) {
                out.accept(data);
            }
        }

        connection.close();

        System.out.println(bufferType.getText());

        String expected = new String(new BufferedInputStream(getClass().getResourceAsStream("/examples/people-100.csv")).readAllBytes());

        MatcherAssert.assertThat(bufferType.getText(), Matchers.is(expected));
    }
}
