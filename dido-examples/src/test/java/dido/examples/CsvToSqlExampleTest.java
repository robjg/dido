package dido.examples;

import dido.csv.DataInCsv;
import dido.csv.DataOutCsv;
import dido.data.DataSchema;
import dido.how.DataIn;
import dido.how.DataOut;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import dido.sql.DataInSql;
import dido.sql.DataOutSql;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
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
                "INDEX integer, " +
                "USER_ID varchar(50), " +
                "FIRST_NAME varchar(50), " +
                "LAST_NAME varchar(50), " +
                "SEX varchar(10), " +
                "EMAIL varchar(50), " +
                "PHONE varchar(50), " +
                "DOB date," +
                "JOB_TITLE varchar(100)" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(create);
        }

        // #snippet1{
        DataSchema schema = DataSchema.builder()
                .addNamed("Index", int.class)
                .addNamed("Date of birth", LocalDate.class)
                .build();

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .conversion(String.class, LocalDate.class, (String s) -> LocalDate.parse(s))
                .make();

        try (DataIn in = DataInCsv.with()
                .header(true)
                .partialSchema(schema)
                .conversionProvider(conversionProvider)
                .fromInputStream(getClass().getResourceAsStream("/examples/people-100.csv"));
             DataOut out = DataOutSql.with()
                     .sql("insert into PEOPLE " +
                             "(INDEX, USER_ID, FIRST_NAME, LAST_NAME, SEX, EMAIL, PHONE, DOB, JOB_TITLE)" +
                             " values (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                     .toConnection(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""))) {

            in.forEach(out);
        }
        // }#snippet1

        // #snippet2{
        StringBuilder stringBuilder = new StringBuilder();

        try (DataIn in = DataInSql.with()
                .sql("select INDEX as \"Index\", " +
                                "USER_ID as \"User Id\", " +
                                "FIRST_NAME as \"First Name\", " +
                                "LAST_NAME as \"Last Name\", " +
                                "SEX as \"Sex\", " +
                                "EMAIL as \"Email\", " +
                                "PHONE as \"Phone\", " +
                                "DOB as \"Date of birth\", " +
                                "JOB_TITLE as \"Job Title\" from PEOPLE"
                )
                .fromConnection(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""));
             DataOut out = DataOutCsv.with()
                     .header(true)
                     .toAppendable(stringBuilder)) {

            in.forEach(out);
        }

        String expected = new String(new BufferedInputStream(
                Objects.requireNonNull(getClass().getResourceAsStream("/examples/people-100.csv")))
                .readAllBytes());

        assertThat(stringBuilder.toString(), is(expected));
        // }#snippet2

        connection.close();
    }
}
