Dido POI
========

Data in and out from Excel.
[DataInPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataInPoi.html)
and [DataOutPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataOutPoi.html)
in the module [dido-poi](../dido-poi) provide a wrapper around [Apache POI](https://poi.apache.org/)

- [Reading from Excel](#reading-from-excel)
- [Reading with a Partial Schema](#reading-with-a-partial-schema)
- [Reading with a Full Schema](#reading-with-a-full-schema)
- [Reading with Headings](#reading-with-headings)
- [Writing to Excel](#writing-to-excel)
- [Oddjob](#oddjob)

### Reading from Excel

Data can be read from Excel
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet1}
The schema has been derived as best can be from the data:
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet2}

### Reading with a Partial Schema

We can provide a partial schema that only overrides the type of certain
fields:
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet3}
Column 2 is now an int.

### Reading with a Full Schema

We can use a full schema that will pick just the columns specified.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet4}
The Schema must match the type of the columns or errors will occur when reading 
the fields.  

### Reading with Headings

We can also read Data with headings
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet5}
Now the schema has the field names taken from the headings.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet6}
We can also provide a partial schema. With heading, the field names must match the schema.
The schema index is not used.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet7}

### Writing to Excel

We can write data To Excel
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet8}
and with a header taken from the Schema
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet9}

### Oddjob

For examples of using Dido POI in Oddjob, see [dido:poi](reference/dido/poi/layouts/DataRows.md)
