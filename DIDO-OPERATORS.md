Dido Operators
==============

Operators exist in [dido-operators](dido-operators) for transforming 
data.

### Field Level Transforms

Given some data:
```java
    DataSchema fromSchema = DataSchema.builder()
            .addNamed("Fruit", String.class)
            .addNamed("Quantity", int.class)
            .addNamed("Price", double.class)
            .build();

    List<DidoData> didoData = DidoData.valuesWithSchema(fromSchema)
            .many()
            .of("Apple", 5, 19.50)
            .of("Orange", 2, 35.24)
            .of("Pear", 3, 26.84)
            .toList();
```

We can write a standard java function to map it to some new data:
```java
        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        Function<DidoData, DidoData> mappingFunc = data -> DataBuilder.newInstance()
                .with("Fruit", data.getStringNamed("Fruit"))
                .with("Qty", data.getIntNamed("Quantity"))
                .with("DiscountPrice", data.getDoubleNamed("Price") * .9)
                .with("BestBefore", bestBeforeDate)
                .build();

        List<DidoData> results = didoData.stream()
                .map(mappingFunc)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
```

This is fine, but will be slow. For each data item the schema is recreated,
and field name access may not be the quickest way of accessing the underlying
data. 

If we know the schema in advance, we can use FieldOps in an OpTransformBuilder to do the same thing:
```java

        DidoTransform transform = OpTransformBuilder.with()
                .copy(true)
                .reIndex(true)
                .forSchema(fromSchema)
                .addOp(FieldOps.rename("Quantity", "Qty"))
                .addOp(FieldOps.map().from("Price").to("DiscountPrice")
                        .with().doubleOp(price -> price * .9))
                .addOp(FieldOps.removeNamed("Price"))
                .addOp(FieldOps.setNamed("BestBefore", bestBeforeDate))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transform)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
```

Because the field ops will find the quickest way of getting and setting the data,
even though we are creating the Transformation with field names, indexes might be
used for the underlying data access.

A DidoTransform is a java function that also provides a resultant schema. Providing 
this schema to a downstream Transformation or Data Out operation may well also 
improve performance.
```java
        DataSchema resultantSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("DiscountPrice", double.class)
                .addNamed("BestBefore", LocalDate.class)
                .build();

        assertThat(transform.getResultantSchema(), is(resultantSchema));

        assertThat(transform.getResultantSchema(), instanceOf(ReadStrategy.class));
```


### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:transform](docs/reference/dido/operators/transform/TransformFactory.md)
