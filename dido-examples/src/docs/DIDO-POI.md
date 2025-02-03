Dido POI
========

Data in and out from Excel.
[DataInPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataInPoi.html)
and [DataOutPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataOutPoi.html)
in the module [dido-poi](dido-poi) provide a wrapper around [Apache POI](https://poi.apache.org/)

Data can be read from Excel
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet1}
The schema has been derived as best can be from the data:
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet2}
We can provide a partial schema that only overrides the type of certain
fields:
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet3}
Column 2 is now an int.

We can use a full schema that will pick just the columns specified.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet4}
The Schema must match the type of the columns or errors will occur when reading 
the fields.  

We can also read Data with headings
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet5}
Now the schema has the field names taken from the headings.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet6}
We can also provide a partial schema. With heading, the field names must match the schema.
The schema index is not used.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet7}

We can write data To Excel
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet8}
and with a header taken from the Schema
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet9}

### Oddjob

For examples of using Dido POI in Oddjob, see [dido:poi](docs/reference/dido/poi/layouts/DataRows.md)
