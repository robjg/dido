Dido
====

Dido stands for Data-In/Data-Out.  and is a framework for reading and
writing data. It is designed to be used within Oddjob but most modules can be used in code
without Oddjob.

Notable modules:

[dido-data](dido-data) The definition of Generic Data on which the rest of Dido is based.

[dido-oddball](dido-oddball) For using Dido in Oddjob.

Formatters: [dido-csv](dido-csv), [dido-json](dido-json), [dido-sql](dido-sql).

Example

```java
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
```



