Dido CSV
========

Comma Separated Values in and out. [DataInCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataInCsv.html)
and [DataOutCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataOutCsv.html)
in the module [dido-csv](../dido-csv) provide a wrapper around [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)

- [Overview](#overview)
- [With Schemas](#with-schemas)
- [Custom-Formats](#custom-formats)
- [Oddjob](#oddjob)

### Overview
We have already seen in the [README](../README.md) an example of Reading a CSV. By default,
all columns are Strings, and the field names are derived. In the last
example a Schema was provided that gave the columns names and types.

In [dido-sql](DIDO-SQL.md) we see that column names can also be taken from
the header of the CSV file, and a partial schema applied which only
overrides the type of specified columns leaving the rest as String.
We also see an Example of writing a CSV including a header line.

### With Schemas

If a schema is provided, and the CSV has a header, the schema field names are used to match the columns.
If there is no header, then the schema indices are used to pick the columns.  
{@oddjob.java.file src/test/java/dido/examples/CsvSchemaExampleTest.java#usingIndicesIn}
Indices are not used when writing data. Just the order of the fields in the schema is important.
{@oddjob.java.file src/test/java/dido/examples/CsvSchemaExampleTest.java#usingIndicesOut}
Adding blank columns can be achieved by using a Transformer. 
{@oddjob.java.file src/test/java/dido/examples/CsvSchemaExampleTest.java#blankColumns}

### Custom Formats

To customise the format, provide a [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html)
as a setting.

When writing:
{@oddjob.java.file src/test/java/dido/examples/CsvDelimitersExampleTest.java#customCsvOut}
When reading:
{@oddjob.java.file src/test/java/dido/examples/CsvDelimitersExampleTest.java#customCsvIn}

### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:csv](reference/dido/csv/CsvDido.md)
