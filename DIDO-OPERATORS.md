Dido Operators
==============

Operators exist in [dido-operators](dido-operators) for transforming 
data.

### Field Level Transforms

We can add and remove fields:
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

        LocalDate bestBeforeDate = LocalDate.parse("2025-01-14");

        DidoTransform transform = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(FieldOps.removeNamed("Quantity"))
                .addOp(FieldOps.setNamed("BestBefore", bestBeforeDate))
                .addOp(FieldOps.setIntNamed("Markup", 20))
                .build();

        List<DidoData> results = didoData.stream()
                .map(transform)
                .collect(Collectors.toList());

        assertThat(results, contains(
                DidoData.of("Apple", 19.50, bestBeforeDate, 20),
                DidoData.of("Orange", 35.24, bestBeforeDate, 20),
                DidoData.of("Pear", 26.84, bestBeforeDate, 20)));
```





### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:transform](docs/reference/dido/operators/transform/TransformFactory.md)
