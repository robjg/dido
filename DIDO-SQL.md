dido-sql
========

Provides very limited support for reading and writing to databases with SQL.
It was created to capture test data to CSV files and reload them.

The data created is just a wrapper around the JDBC Result Set. The data will change as
the underlying result set next is called, so it should always be copied if it is to be 
used outside the streams iterator.

Example Reading Loading a Table from a CSV file:

```java
        DataSchema schema = DataSchema.builder()
                .addNamed("Index", int.class)
                .addNamed("Date of birth", LocalDate.class)
                .build();

        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .conversion(String.class, LocalDate.class, (String s) -> LocalDate.parse(s))
                .make();

        try (DataIn in = DataInCsv.with()
                .header(true)
                .schema(schema)
                .partialSchema(true)
                .conversionProvider(conversionProvider)
                .fromInputStream(getClass().getResourceAsStream("/examples/people-100.csv"));
             DataOut out = DataOutSql.with()
                     .sql("insert into PEOPLE " +
                             "(INDEX, USER_ID, FIRST_NAME, LAST_NAME, SEX, EMAIL, PHONE, DOB, JOB_TITLE)" +
                             " values (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                     .toConnection(DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", ""))) {

            in.stream().forEach(out);
        }
```


Example Reading a table and creating a CSV that matches the original:

```java
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

            for (DidoData data : in) {
                out.accept(data);
            }
        }

        String expected = new String(new BufferedInputStream(
                Objects.requireNonNull(getClass().getResourceAsStream("/examples/people-100.csv")))
                .readAllBytes());

        assertThat(stringBuilder.toString(), is(expected));
```

