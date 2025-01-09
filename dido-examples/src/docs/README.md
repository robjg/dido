Dido
====

Dido stands for Data-In/Data-Out. It is a framework for making data from different sources
look the same so that it can be copied, processed and compared.

Dido is available in Maven. To get started simply include [dido-all](https://mvnrepository.com/artifact/uk.co.rgordon/dido-all)
which will provide all the stable modules in one dependency.

### Some Examples

Given this CSV:
{@oddjob.text.file  src/test/resources/data/FruitNoHeader.csv}
We can read it in:
{@oddjob.java.file src/test/java/dido/examples/ReadmeExamplesTest.java#snippet1}
And we can write it out as Json
{@oddjob.java.file src/test/java/dido/examples/ReadmeExamplesTest.java#snippet2}
Giving us:
{@oddjob.text.file src/test/resources/data/FruitAllText.json}
We can give our data a schema:
{@oddjob.java.file src/test/java/dido/examples/ReadmeExamplesTest.java#snippet3}
And now when we copy from CSV to JSON
{@oddjob.java.file src/test/java/dido/examples/ReadmeExamplesTest.java#snippet4}
We get:
{@oddjob.text.file src/test/resources/data/FruitLines.json}


### More Info

[dido-data](DIDO-DATA.md) The definition of Data on which the rest of Dido is based.

Formatters: 
 - [dido-csv](dido-csv) - For reading and writing CSV data.  
 - [dido-json](dido-json) - For reading and writing JSON. 
 - [dido-sql](dido-sql) - For reading and writing to Databases.
 - [dido-poi](dido-poi) - For reading and writing to Excel sheets.
 - [dido-text](dido-text) - For writing to Ascii Formatted Text Tables.

[dido-oddball](dido-oddball) For using Dido in Oddjob.


