Dido Object
===========

Serialize Java Objects to Dido Data, and deserialize Dido Data
to Java Objects.

Serializing
-----------

Given an Object with standard Java Getters:
```java
package dido.examples.objects;

public class Apple {

    public String getFruit() {
        return "Apple";
    }

    public int getQty() {
        return 5;
    }

    public double getPrice() {
        return 19.5;
    }
}
```

We can serialize this to DidoData:
```java
        DidoData didoData = DataInObjects
                .beanOf(Apple.class)
                .toDidoData(new Apple());

        assertThat(didoData.getStringNamed("fruit"), is("Apple"));
        assertThat(didoData.getIntNamed("qty"), is(5));
        assertThat(didoData.getDoubleNamed("price"), is(19.5));
```

Java doesn't give us any control over the order methods are
provided so we don't know the order of the fields in the data,
we can only access the data by name. We can solve this
problem by providing the properties in the order we want in the data:
```java
        DidoData didoData = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .toDidoData(new Apple());

        assertThat(didoData, is(DidoData.of("Apple", 5, 19.5)));
        assertThat(didoData.getSchema().toString(), is("{[1:fruit]=java.lang.String, [2:qty]=int, [3:price]=double}"));
```


We can also serialize a Java Collection of beans using a `DataIn` in a similar way
to other formatters:
```java
        try (DataIn dataIn = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .inFrom(List.of(new Apple(), new Apple(), new Apple()))) {

            List<DidoData> didoData = dataIn
                .stream().collect(Collectors.toList());

            assertThat(didoData, contains(
                    DidoData.of("Apple", 5, 19.5),
                    DidoData.of("Apple", 5, 19.5),
                    DidoData.of("Apple", 5, 19.5)));
        }
```

However in most situations using the `mapper()` method will be more natural:
```java
        List<DidoData> didoData = Stream.of(new Apple(), new Apple(), new Apple())
                .map(DataInObjects
                        .beanOf(Apple.class)
                        .fields("fruit", "qty", "price")
                        .mapper())
                .collect(Collectors.toList());
```


Deserializing
-----------

Given an Object with standard Java Setters:
```java
package dido.examples.objects;

public class FruitBean {
    private String fruit;
    private int qty;
    private double price;

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "FruitBean{" +
                "fruit='" + fruit + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}
```

We can create this from DidoData:
```java
        DidoData didoData = DidoData.builder()
                .withString("fruit", "Apple")
                .withInt("qty", 5)
                .withDouble("price", 19.5)
                .build();

        FruitBean fruit = DataOutObjects
                .beanOf(FruitBean.class)
                .fromDidoData(didoData);

        assertThat(fruit.toString(), is("FruitBean{fruit='Apple', qty=5, price=19.5}"));
```

The data must have field names that match the properties we want to set,
that's why we used the builder here to create the DidoData.

We can also consume a Collection of DidoData as Objects in a `DataOut`:
```java
        List<FruitBean> fruitBeans = new LinkedList<>();

        DataSchema schema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        List<DidoData> didoData = DidoData.withSchema(schema)
                .many()
                .of("Apple", 5, 19.5)
                .of("Orange", 10, 31.6)
                .of("Pear", 7, 22.1)
                .toList();

        try (DataOut dataOut = DataOutObjects
                .beanOf(FruitBean.class)
                .schema(schema)
                .<FruitBean>outTo(fruitBeans::add)) {

            didoData.forEach(dataOut);
        }

        assertThat(fruitBeans.stream()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                contains(
                        "FruitBean{fruit='Apple', qty=5, price=19.5}",
                        "FruitBean{fruit='Orange', qty=10, price=31.6}",
                        "FruitBean{fruit='Pear', qty=7, price=22.1}"));
```

Providing the schema as we have done here speeds things up slightly
as the transformation can be calculated once up front and
not done on the fly from the data as it would otherwise have to be.

This chained consumer pipeline is useful for an asynchronous subscription
scenario, however again a `mapper()` is probably more useful:
```java
        fruitBeans = didoData.stream()
                .map(DataOutObjects
                        .beanOf(FruitBean.class)
                        .schema(schema)
                        .<FruitBean>mapper())
                .collect(Collectors.toList());
```

Which achieves the same as the above.
