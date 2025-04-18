Dido CSV
========

Comma Separated Values in and out. [DataInCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataInCsv.html)
and [DataOutCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataOutCsv.html)
in the module [dido-csv](../dido-csv) provide a wrapper around [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)

We have already seen in the [README](../README.md) an example of Reading a CSV. By default,
all columns are Strings, and the field names are derived. In the last
example a Schema was provided that gave the columns names and types.

In [dido-sql](DIDO-SQL.md) we see that column names can also be taken from
the header of the CSV file, and a partial schema applied which only
overrides the type of specified columns leaving the rest as String.
We also see an Example of writing a CSV including a header line.

### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:csv](reference/dido/csv/CsvDido.md)
