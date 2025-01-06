Dido Data
=========

[DidoData](http://rgordon.co.uk/projects/dido/current/api/dido/data/DidoData.html) 
defines the data format common to all sources of data. Data is accessible via an
index or a field name. Here's an example of `DidoData` read from JSON:
```java
        DidoData data = DataInJson.with()
                .schema(DataSchema.builder()
                        .addNamed("Qty", int.class)
                        .build())
                .partialSchema(true)
                .mapFromString()
                .apply("{ \"Fruit\"=\"Apple\", \"Qty\"=5, \"Price\"=27.2 }");

        assertThat(data.getAt(1), is("Apple"));
        assertThat(data.getAt(2), is(5));
        assertThat(data.getAt(3), is(27.2));

        assertThat(data.getNamed("Fruit"), is("Apple"));
        assertThat(data.getNamed("Qty"), is(5));
        assertThat(data.getNamed("Price"), is(27.2));
```

Non-boxing access is available for all primitive types, and String is added as a convenience.
```java
        assertThat(data.getStringAt(1), is("Apple"));
        assertThat(data.getIntAt(2), is(5));
        assertThat(data.getDoubleAt(3), is(27.2));

        assertThat(data.getStringNamed("Fruit"), is("Apple"));
        assertThat(data.getIntNamed("Qty"), is(5));
        assertThat(data.getDoubleNamed("Price"), is(27.2));
```

Use of boxed types is implementation specific, so using the primitive accessors may
not always yield a performance improvement. 

`DidoData` always has a [DataSchema](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataSchema.html) that
defines its layout.
```java
        DataSchema schema = data.getSchema();

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getTypeAt(3), is(double.class));

        assertThat(schema.getTypeNamed("Fruit"), is(String.class));
        assertThat(schema.getTypeNamed("Qty"), is(int.class));
        assertThat(schema.getTypeNamed("Price"), is(double.class));
```

A `DataSchema` consists of [SchemaField](http://rgordon.co.uk/projects/dido/current/api/dido/data/SchemaField.html)s
A `SchemaField` of a `DataSchema` will always hav an index > 0, a non-null field name, 
and a type. No constraints are put on the type data. Nested `DidoData` and 
repeating nested `DidoData` are also supported, although to what extent these 
are implemented by the formatters is limited. For instance, there is currently no way to
return a nested `DidoData` field from the column of a CSV file. 

Various implementations of `DidoData` exist and include
[MapData](http://rgordon.co.uk/projects/dido/current/api/dido/data/MapData.html) and
[ArrayData](http://rgordon.co.uk/projects/dido/current/api/dido/data/ArrayData.html)
amongst others. These are immutable and thread safe implementations.

Convenience static creation methods exist on `DidoData` which will use the implementation 
that is considered the most performant for general use in any release.

`DidoData` can be created from field values:
```java
        DidoData data = DidoData.of("Apple", 5, 15.6);

        assertThat(data.toString(), is("{[1:f_1]=Apple, [2:f_2]=5, [3:f_3]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:f_1]=java.lang.String, [2:f_2]=java.lang.Integer, [3:f_3]=java.lang.Double}"));
```

Or using a builder:
```java
        DidoData data = DidoData.builder()
                .with("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 15.6)
                .build();

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
```

(Note the none-boxed types here)

A `DataSchema` can also be created with a builder:
```java
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();
```

And now we can create data matching the schema from field values:
```java
        DidoData data = DidoData.valuesWithSchema(schema)
                .of("Apple", 5, 15.6);

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
```

Or a builder:
```java
        DidoData data = DidoData.builderForSchema(schema)
                .with("Fruit", "Apple")
                .withInt("Qty", 5)
                .withDouble("Price", 15.6)
                .build();

        assertThat(data.toString(), is("{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=15.6}"));

        assertThat(data.getSchema().toString(), is("{[1:Fruit]=java.lang.String, [2:Qty]=int, [3:Price]=double}"));
```


Not all implementations of `DidoData` are immutable. That which
wraps a `ResultSet` returned from a SQL query is not. This is to allow
a simple single threaded copy to be as performant as possible. To ensure 
thread safety data should be copied to an immutable type.  
