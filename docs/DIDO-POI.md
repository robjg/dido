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
                is("{[1:f_1]=java.lang.String, [2:f_2]=double, [3:f_3]=double}"));
```


### Reading with a Partial Schema

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
                is("{[1:f_1]=java.lang.String, [2:f_2]=int, [3:f_3]=double}"));
```

Column 2 is now an int.

### Reading with a Full Schema

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

### Reading with Headings

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
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=double, [3:Price]=double}"));
```

We can also provide a partial schema. With heading, the field names must match the schema.
The schema index is not used.
```java
        try (DataIn in = DataInPoi.with()
                .partialSchema(DataSchema.builder()
                        .addNamed("Quantity", int.class)
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
                is("{[1:Fruit]=java.lang.String, [2:Quantity]=int, [3:Price]=double}"));
```


### Writing to Excel

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

        List<DidoData> didoData = DidoData.withSchema(schema)
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

For examples of using Dido POI in Oddjob, see [dido:poi](reference/dido/poi/layouts/DataRows.md)
