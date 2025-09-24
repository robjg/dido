Dido JSON
=========

JSON in and out. [DataInJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataInJson.html)
and [DataOutJson](http://rgordon.co.uk/projects/dido/current/api/dido/json/DataOutJson.html)
in the module [dido-json](../dido-json) provide a wrapper around [GSON](https://github.com/google/gson).

- [Reading](#reading)
- [Schemas](#schemas)
- [Conversions](#conversions)
- [Copying Data](#copying-data)
- [Oddjob](#oddjob)

### Reading

We have already seen in the [README](../README.md) an example of writing JSON.

Here's an example of reading that JSON back in.
```java
        List<DidoData> didoData;

        try (DataIn in = DataInJson
                .with().strictness(Strictness.LENIENT)
                .fromPath(Path.of("Fruit.json"))) {

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
                is("{[1:Fruit]=java.lang.String, [2:Qty]=double, [3:Price]=double}"));
```


### Schemas

We can provide a partial schema that only overrides the type of certain
fields:
```java
        try (DataIn in = DataInJson.with()
                .strictness(Strictness.LENIENT)
                .partialSchema(DataSchema.builder()
                        .addNamed("Qty", int.class)
                        .build())
                .fromPath(Path.of("Fruit.json"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
```

Or a full schema that will pick just the fields specified.
```java
        try (DataIn in = DataInJson.with()
                .strictness(Strictness.LENIENT)
                .schema(DataSchema.builder()
                        .addNamed("Fruit", String.class)
                        .addNamed("Price", double.class)
                        .build())
                .fromPath(Path.of("Fruit.json"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 19.50),
                DidoData.of("Orange", 35.24),
                DidoData.of("Pear", 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Price]=double}"));
```


### Conversions
Using the `GsonBuilder` you can add conversions as documented in the 
[GSON user guide](https://google.github.io/gson/UserGuide.html). Here's an example.

Given this JSON string:
```java
        String json = "{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }";
```

If we want `BestBefore` to be `java.time.LocalDate` we can use a GSON Deserializer:
```java
        class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
            }
        }

        DidoData data = DataInJson.with()
                .gsonBuilder(gsonBuilder ->
                        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()))
                .partialSchema(DataSchema.builder()
                        .addNamed("BestBefore", LocalDate.class)
                        .build())
                .mapFromString()
                .apply(json);

        assertThat(data, is(DidoData.of("Apple", LocalDate.parse("2025-02-14"))));
        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:BestBefore]=java.time.LocalDate}"));
```

Similarly we can go back to JSON with a GSON Serializer
```java
        class LocalDateSerializer implements JsonSerializer<LocalDate> {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE));
            }
        }

        String jsonAgain = DataOutJson.with()
                .gsonBuilder(gsonBuilder ->
                        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()))
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }", jsonAgain,
                JSONCompareMode.STRICT);
```

Often we will already have a `DidoConversionProvider` available
```java
        DidoConversionProvider conversionProvider = DefaultConversionProvider.with()
                .<String, LocalDate>conversion(String.class, LocalDate.class, LocalDate::parse)
                .make();
```

We can tell our `DataInJson` to use a Dido Conversion:
```java
        DidoData data = DataInJson.with()
                .conversionProvider(conversionProvider)
                .didoConversion(String.class, LocalDate.class)
                .partialSchema(DataSchema.builder()
                        .addNamed("BestBefore", LocalDate.class)
                        .build())
                .mapFromString()
                .apply(json);

        assertThat(data, is(DidoData.of("Apple", LocalDate.parse("2025-02-14"))));
        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:BestBefore]=java.time.LocalDate}"));
```

And likewise our `DataOutJson`:
```java
        String jsonAgain = DataOutJson.with()
                .conversionProvider(conversionProvider)
                .didoConversion(LocalDate.class, String.class)
                .mapToString()
                .apply(data);

        JSONAssert.assertEquals("{ 'Fruit': 'Apple', 'BestBefore': '2025-02-14' }", jsonAgain,
                JSONCompareMode.STRICT);
```


### Copying Data

The API supports providing an [DataFactoryProvider](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataFactoryProvider.html)
that will copy data as it is parsed. This is historical, as it was easier to develop 
than the Wrapping Data style that follows Dido's philosophy better.
This will be deprecated at some point in future releases.

### Oddjob

For examples of using Dido JSON in Oddjob, see [dido:json](reference/dido/json/JsonDido.md)
