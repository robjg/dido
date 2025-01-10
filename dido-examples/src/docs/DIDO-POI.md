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
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet2}

We can use a full schema that will pick just the columns specified.
{@oddjob.java.file src/test/java/dido/examples/PoiExamplesTest.java#snippet2}
Here we get the correct type. Note the column index is maintained.


### Oddjob

For examples of using Dido POI in Oddjob, see [dido:poi](docs/reference/dido/poi/layouts/DataRows.md)
