Dido
====

Dido stands for Data-In/Data-Out. It is a framework for making data from different sources
look the same so that it can be copied, processed and compared.

Dido is available in Maven. To get started simply include [dido-all](https://mvnrepository.com/artifact/uk.co.rgordon/dido-all)
which will provide all the stable modules in one dependency.

### Some Examples

Given this CSV:
```
Apple,5,19.50
Orange,2,35.24
Pear,3,26.84
```

We can read it in:
```java
        List<DidoData> didoData;

        try (DataIn in = DataInCsv.fromPath(Path.of("Fruit.csv"))) {

            didoData = in.stream().collect(Collectors.toList());
        }

        assertThat(didoData, contains(
                DidoData.of("Apple", "5", "19.50"),
                DidoData.of("Orange", "2", "35.24"),
                DidoData.of("Pear", "3", "26.84")));
```

And we can write it out as Json
```java
        try (DataOut out = DataOutJson.toOutputStream(System.out)) {

            didoData.forEach(out);
        }
```

Giving us:
```
{"f_1":"Apple","f_2":"5","f_3":"19.50"}{"f_1":"Orange","f_2":"2","f_3":"35.24"}{"f_1":"Pear","f_2":"3","f_3":"26.84"}
```

We can give our data a schema:
```java
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();
```

And now when we copy from CSV to JSON
```java
        try (DataIn in = DataInCsv.with()
                .schema(schema)
                .fromPath(Path.of("Fruit.csv"));
             DataOut out = DataOutJson.with()
                     .outFormat(JsonDidoFormat.LINES)
                     .toOutputStream(System.out)) {

            in.forEach(out);
        }
```

We get:
```
{"Fruit":"Apple","Qty":5,"Price":19.5}
{"Fruit":"Orange","Qty":2,"Price":35.24}
{"Fruit":"Pear","Qty":3,"Price":26.84}
```



### More Info

[dido-data](DIDO-DATA.md) provides the definition of Data on which the rest of Dido is based.
[dido-operators](DIDO-OPERATORS.md) provide functions for processing data.

For Reading Data in and Out in different formats: 
 - [dido-csv](DIDO-CSV.md) - For reading and writing CSV data.  
 - [dido-json](DIDO-JSON.md) - For reading and writing JSON. 
 - [dido-sql](DIDO-SQL.md) - For reading and writing to Databases.
 - [dido-poi](DIDO-POI.md) - For reading and writing to Excel sheets.
 - [dido-text](DIDO-TEXT.md) - For writing to Ascii Formatted Text Tables.

For using Dido in [Oddjob](http://rgordon.co.uk/oddjob) there is [dido-oddball](DIDO-ODDBALL.md) .


