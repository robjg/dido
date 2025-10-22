Dido Operators
==============

Operators exist in [dido-operators](../dido-operators) for transforming 
data.

- [Field Transforms - The Slow Way](#field-transforms---the-slow-way)
- [Field Transforms - As View Or Copy](#field-transforms---as-a-view-or-copy)
- [Field Transforms - Resultant Schema](#field-transforms---resultant-schema)
- [Concatenating Data](#concatenating-data)
- [Removing Data](#removing-data)
- [Oddjob](#oddjob)

### Field Transforms - The Slow Way

Given some data:
```java
    DataSchema fromSchema = DataSchema.builder()
            .addNamed("Fruit", String.class)
            .addNamed("Quantity", int.class)
            .addNamed("Price", double.class)
            .build();

    List<DidoData> didoData = DidoData.withSchema(fromSchema)
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

### Field Transforms - As A View or Copy

If we know the schema in advance, we can use `FieldViews` and an `ViewTransformBuilder` to do the same thing:
```java
        DidoTransform transform = ViewTransformBuilder.with()
                .existingFields(true)
                .reIndex(true)
                .forSchema(fromSchema)
                .addFieldView(FieldViews.rename("Quantity", "Qty"))
                .addFieldView(FieldViews.map().from("Price").to("DiscountPrice")
                        .with().doubleOp(price -> price * .9))
                .addFieldView(FieldViews.removeNamed("Price"))
                .addFieldView(FieldViews.setNamed("BestBefore", bestBeforeDate))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transform)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
```

The resultant data is a view of the underlying data. This is often the most performant way of reading data, 
changing a few things, and writing it out again. However, if the data is read more than once, discounting our 
price will be done with each read, and if our view is of mutable data (such as that from a SQL query) and
is not immediately written out then we need to copy the data during the transformation.

This can be achieved using `WriteTransformBuilder` in exactly the same way.
```java
        DidoTransform transform = WriteTransformBuilder.with()
                .existingFields(true)
                .reIndex(true)
                .forSchema(fromSchema)
                .addFieldView(FieldViews.rename("Quantity", "Qty"))
                .addFieldView(FieldViews.map().from("Price").to("DiscountPrice")
                        .with().doubleOp(price -> price * .9))
                .addFieldView(FieldViews.removeNamed("Price"))
                .addFieldView(FieldViews.setNamed("BestBefore", bestBeforeDate))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transform)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 5, 17.55, bestBeforeDate),
                DidoData.of("Orange", 2, 31.716, bestBeforeDate),
                DidoData.of("Pear", 3, 24.156, bestBeforeDate)));
```

In both instance the operations will find the quickest way of reading the data and in the latter,
of setting the data.
Even though we are creating the Transformation with field names, indexes might be
used for the underlying data access which has been observed to yield a 4 times performance improvement.

### Field Transforms - Resultant Schema

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


### Concatenating Data

We can concatenate data:
```java
        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        DidoData bestBeforeData = DidoData.of(bestBeforeDate);

        Concatenator concatenator = Concatenator.fromSchemas(
                fromSchema, bestBeforeData.getSchema());

        List<DidoData> results = didoData.stream()
                .map(data -> concatenator.concat(data, bestBeforeData))
                .toList();

        assertThat(results, contains(
                DidoData.of("Apple", 5, 19.50, bestBeforeDate),
                DidoData.of("Orange", 2, 35.24, bestBeforeDate),
                DidoData.of("Pear", 3, 26.84, bestBeforeDate)));
```


### Removing Data

We can create sub data: 
```java
        DidoTransform subData = SubData.asMappingFrom(fromSchema)
                .excludingNames("Quantity");

        List<DidoData> results = didoData.stream()
                .map(subData)
                .toList();

        assertThat(results, contains(
                DidoData.of("Apple", 19.50),
                DidoData.of("Orange", 35.24),
                DidoData.of("Pear", 26.84)));
```

This creates a view on the data. No underlying data is changed. It may be 
slightly more performant than doing the same with field level removes but
this has not been proven.

### Oddjob

For examples of using Dido Operators in Oddjob, see [dido:transform](reference/dido/operators/transform/TransformationFactory.md)
