Dido POI
========

Data in and out from Excel.
[DataInPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataInPoi.html)
and [DataOutPoi](http://rgordon.co.uk/projects/dido/current/api/dido/poi/DataOutPoi.html)
in the module [dido-poi](dido-poi) provide a wrapper around [Apache POI](https://poi.apache.org/)

Data can be read from Excel
```java
        List<DidoData> didoData;

        try (DataIn in = DataInPoi.fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84)));
```

The schema has been derived as best can be from the data:
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Double, [3:f_3]=java.lang.Double}"));
```

We can provide a partial schema that only overrides the type of certain
fields:
```java
        try (DataIn in = DataInPoi.with()
                .partialSchema(DataSchema.builder()
                        .addAt(2, int.class)
                        .build())
                .fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=int, [3:f_3]=java.lang.Double}"));
```


We can use a full schema that will pick just the columns specified.
```java
        try (DataIn in = DataInPoi.with()
                .schema(DataSchema.builder()
                        .addNamedAt(1, "Fruit", String.class)
                        .addNamedAt(3, "Price", double.class)
                        .build())
                .fromPath(Path.of("Fruit.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 19.50),
                DidoData.of("Orange", 35.24),
                DidoData.of("Pear", 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [3:Price]=double}"));
```

The Schema must match the type of the columns or errors will occur when reading 
the fields.  

We can also read Data with headings
```java
        List<DidoData> didoData;

        try (DataIn in = DataInPoi.with()
                .header(true)
                .fromPath(Path.of("FruitWithHeadings.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84)));
```

Now the schema has the field names taken from the headings.
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=java.lang.Double, [3:Price]=java.lang.Double}"));
```

We can also provide a partial schema. With heading, the field names must match the schema.
The schema index is not used.
```java
        try (DataIn in = DataInPoi.with()
                .partialSchema(DataSchema.builder()
                        .addNamedAt(2, "Quantity", int.class)
                        .build())
                .header(true)
                .fromPath(Path.of("FruitWithHeadings.xlsx"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        System.out.println(didoData.get(0).getSchema().toString());
        System.out.println(didoData);

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.50),
                DidoData.of("Orange", 2, 35.24),
                DidoData.of("Pear", 3, 26.84)));

        assertThat(didoData.get(0).getSchema().toString(),
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=int, [3:Price]=java.lang.Double}"));
```


We can write data To Excel
```java
        List<DidoData> didoData = List.of(
                DidoData.of("Apple", 5.0, 19.50),
                DidoData.of("Orange", 2.0, 35.24),
                DidoData.of("Pear", 3.0, 26.84));

        try (DataOut out = DataOutPoi.toPath(Path.of("Fruit.xlsx"))) {
            didoData.forEach(out);
        }
```

and with a header taken from the Schema
```java
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Quantity", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = DidoData.valuesWithSchema(schema)
                .many()
                .of("Apple", 5, 19.50)
                .of("Orange", 2, 35.24)
                .of("Pear", 3, 26.84)
                .toList();

        try (DataOut out = DataOutPoi.with()
                .header(true)
                .toPath(Path.of("FruitWithHeadings.xlsx"))) {
            didoData.forEach(out);
        }
```


### Oddjob

For examples of using Dido POI in Oddjob, see [dido:poi](docs/reference/dido/poi/layouts/DataRows.md)
