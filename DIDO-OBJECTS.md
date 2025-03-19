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

We can also serialize a Java Stream of beans:
```java
        List<DidoData> didoData = DataInObjects
                .beanOf(Apple.class)
                .fields("fruit", "qty", "price")
                .inFrom(Stream.of(new Apple(), new Apple(), new Apple()))
                .stream().collect(Collectors.toList());

        assertThat(didoData, contains(
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5),
                DidoData.of("Apple", 5, 19.5)));
```



