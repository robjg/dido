Dido JSON
=========

JSON in and out. [DataInJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataInJson.html)
and [DataOutJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataOutJson.html)
in the module [dido-json](dido-json) provide a wrapper around [GSON](https://github.com/google/gson).

### Reading

We have already seen in the [README](README.md) an example of writing JSON.

Here's an example of reading that JSON back in.
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet1}
The schema has been derived from the data:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet2}
We can provide a partial schema that only overrides the type of certain
fields:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet3}
Or a full schema that will pick just the fields specified.
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#snippet4}

### Conversions
Using the `GsonBuilder` you can add conversions as documented in the 
[GSON user guide](https://google.github.io/gson/UserGuide.html). Here's an example.

Given this JSON string:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionJson}
If we want `BestBefore` to be `java.time.LocalDate` we can use a GSON Deserializer:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionDeserializer}
Similarly we can go back to JSON with a GSON Serializer
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionSerializer}
Often we will already have a `DidoConversionProvider` available
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionDido}
We can tell our `DataInJson` to use a Dido Conversion:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionDidoIn}
And likewise our `DataOutJson`:
{@oddjob.java.file src/test/java/dido/examples/JsonExamplesTest.java#conversionDidoOut}

### Copying Data.

The API supports providing an [DataFactoryProvider](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataFactoryProvider.html)
that will copy data as it is parsed. This is historical, as it was easier to develop 
than the Wrapping Data style that follows Dido's philosophy better.
This will be deprecated at some point in future releases.

### Oddjob

For examples of using Dido JSON in Oddjob, see [dido:json-stream](docs/reference/dido/json/JsonDido.md)
