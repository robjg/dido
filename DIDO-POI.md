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
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Double, [3:f_3]=java.lang.Double}"));
```


We can use a full schema that will pick just the columns specified.
```java
        DataSchema schema = didoData.get(0).getSchema();

        assertThat(schema.toString(),
                is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Double, [3:f_3]=java.lang.Double}"));
```

Here we get the correct type. Note the column index is maintained.


### Oddjob

For examples of using Dido POI in Oddjob, see [dido:poi](docs/reference/dido/poi/layouts/DataRows.md)
