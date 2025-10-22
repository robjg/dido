Dido Text
========

Out to an Ascii Text Table. [DataOutTextTable](http://rgordon.co.uk/projects/dido/current/api/dido/text/DataOutTextTable.html)
in the module [dido-text](../dido-text) provide a wrapper around [org.nocrala.tools.texttablefmt/text-table-formatter](https://mvnrepository.com/artifact/org.nocrala.tools.texttablefmt/text-table-formatter)

- [Writing a Table Table](#writing-a-text-table)
- [Oddjob](#oddjob)

### Writing a Text Table

Here's an example of writing. Given this schema and data:
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
```

We can write it to a text table with:
```java
        try (DataOut out = DataOutTextTable.toOutputStream(System.out)) {

            didoData.forEach(out);
        }
```

Giving this result:
```
Fruit |Quantity|Price
------+--------+-----
Apple |       5| 19.5
Orange|       2|35.24
Pear  |       3|26.84
```



### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:table](reference/dido/text/TextTableDido.md)
