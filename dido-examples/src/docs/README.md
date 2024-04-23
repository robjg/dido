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

{@oddjob.java.file src/test/java/dido/examples/CsvToSqlExampleTest.java#snippet1}


