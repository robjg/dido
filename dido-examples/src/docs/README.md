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

[dido-data](docs/DIDO-DATA.md) provides the definition of Data on which the rest of Dido is based.
[dido-operators](docs/DIDO-OPERATORS.md) provide functions for processing data.

For Reading Data in and Out in different formats: 
 - [dido-csv](docs/DIDO-CSV.md) - For reading and writing CSV data.  
 - [dido-json](docs/DIDO-JSON.md) - For reading and writing JSON. 
 - [dido-sql](docs/DIDO-SQL.md) - For reading and writing to Databases.
 - [dido-poi](docs/DIDO-POI.md) - For reading and writing to Excel sheets.
 - [dido-text](docs/DIDO-TEXT.md) - For writing to Ascii Formatted Text Tables.

[dido-objects](docs/DIDO-OBJECTS.md) for converting to and from Java Objects.

For using Dido in [Oddjob](http://rgordon.co.uk/oddjob) there is [dido-oddball](docs/DIDO-ODDBALL.md) .


