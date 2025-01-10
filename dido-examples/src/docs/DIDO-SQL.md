Dido SQL
========

[DataInSql](http://rgordon.co.uk/projects/dido/current/api/dido/sql/DataInSql.html)
and [DataOutSql](http://rgordon.co.uk/projects/dido/current/api/dido/sql/DataOutSql.html)
in the module [dido-sql](dido-sql)
provide very limited support for reading and writing to databases with SQL.
It was created to capture test data to CSV files and reload them.

The data created is just a wrapper around the JDBC Result Set. The data will change as
the underlying result set next is called, so it should always be copied if it is to be 
used outside the streams iterator.

Here's an example Loading a Table from a CSV file:

{@oddjob.java.file src/test/java/dido/examples/CsvToSqlExampleTest.java#snippet1}

And now reading that table and creating a CSV that matches the original:

{@oddjob.java.file src/test/java/dido/examples/CsvToSqlExampleTest.java#snippet2}

### Oddjob

For examples of using Dido Sql in Oddjob, see [dido:sql](docs/reference/dido/sql/SqlDido.md)

