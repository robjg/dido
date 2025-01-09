Dido JSON
=========

JSON in and out. Provides a wrapper around
[GSON](https://github.com/google/gson).

We have already seen in the [README](README.md) an example of writing JSON.

Here's an example of reading that JSON back in.
```java
        List<DidoData> didoData;

        try (DataIn in = DataInJson.fromPath(Path.of("Fruit.json"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84)));
```

The schema has been derived from the data:
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:Fruit]=java.lang.String, [2:Qty]=java.lang.Double, [3:Price]=java.lang.Double}"));
```

We can provide a partial schema that only overrides the type of certain
fields:
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:Fruit]=java.lang.String, [2:Qty]=java.lang.Double, [3:Price]=java.lang.Double}"));
```

Or a full schema that will pick just the fields specified.
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:Fruit]=java.lang.String, [2:Qty]=java.lang.Double, [3:Price]=java.lang.Double}"));
```


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
