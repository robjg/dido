Dido
====

- [Overview](#overview)
- [Some Java Examples](#some-java-examples)
- [No Code Dido](#no-code-dido)
- [More Info](#more-info)
- [Building](#building)
- [Background](#background)

### Overview

Dido stands for Data-In/Data-Out. It is a framework for making data from different sources
look the same so that it can be copied, processed and compared.

Dido is available in Maven. To get started simply include [dido-all](https://mvnrepository.com/artifact/uk.co.rgordon/dido-all)
which will provide all the stable modules in one dependency.

### Some Java Examples

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

### No Code Dido

Dido comes with Jobs and Types for creating Data Processing Pipelines in [Oddjob](https://github.com/robjg/oddjob/)
without code using Oddjob's UI - *Oddjob Explorer*

Here is Oddjob Explorer running the first example above.

![Csv to Json in Oddjob](docs/images/OddjobCsvJson.jpg)

See [Dido in Oddjob](docs/DIDO-ODDJOB.md) for getting started with Dido in Oddjob.

See [The Reference](docs/reference/README.md) for details of all the Oddjob configurations in Dido. 

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


### Building

See [Building](BUILDING.md)

### Background

For a more information on why Dido was created please see
[Background](BACKGROUND.md)

