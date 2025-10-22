Dido SQL
========

[DataInSql](http://rgordon.co.uk/projects/dido/current/api/dido/sql/DataInSql.html)
and [DataOutSql](http://rgordon.co.uk/projects/dido/current/api/dido/sql/DataOutSql.html)
in the module [dido-sql](../dido-sql)

- [Overview](#overview)
- [Writing to DB](#writing-to-db)
- [Reading from DB](#reading-from-db)
- [Oddjob](#oddjob)

### Overview

Dido provides very limited support for reading and writing to databases with SQL.
It was created to compare Tables using [Beancmpr](https://github.com/robjg/beancmpr) 
and to capture test data to CSV files and reload them. For more complex
Database to Java problems, there are numerous and better frameworks.
We have no intention of trying to compete.

### Writing to DB

Here's an example Loading a Table from a CSV file:
{@oddjob.java.file src/test/java/dido/examples/CsvToSqlExampleTest.java#snippet1}

## Reading from DB

And now reading that table and creating a CSV that matches the original:
{@oddjob.java.file src/test/java/dido/examples/CsvToSqlExampleTest.java#snippet2}
The data created is just a wrapper around the JDBC Result Set. The data will change as
the underlying result set `next()` is called, so it should always be copied if it is to be
used outside the streams iterator.

### Oddjob

For examples of using Dido Sql in Oddjob, see [dido:sql](reference/dido/sql/SqlDido.md)

