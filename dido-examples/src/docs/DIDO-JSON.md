Dido JSON
=========

JSON in and out. [DataInJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataInJson.html)
and [DataOutJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataOutJson.html)
in the module [dido-json](dido-json) provide a wrapper around [GSON](https://github.com/google/gson).

We have already seen in the [README](README.md) an example of writing JSON.

Here's an example of reading that JSON back in.
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet1}
The schema has been derived from the data:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet2}
We can provide a partial schema that only overrides the type of certain
fields:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet2}
Or a full schema that will pick just the fields specified.
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet2}

### Copying Data.

The API supports providing an [DataFactoryProvider](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataFactoryProvider.html)
that will copy data as it is parsed. This is historical, as it was easier to develop 
than the Wrapping Data style that follows Dido's philosophy better.
This will be deprecated at some point in future releases.

### Conversion

Conversion is poorly supported compared with other formats. This needs to be
fixed.

### Oddjob

For examples of using Dido JSON in Oddjob, see [dido:json-stream](docs/reference/dido/json/JsonDido.md)
