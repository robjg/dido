Dido Data
=========

[DidoData](http://rgordon.co.uk/projects/dido/current/api/dido/data/DidoData.html) 
defines the data format common to all sources of data. 

- [Overview](#overview)
- [Data Schemas](#data-schemas)
- [Instances](#instances-of-dido-data)
- [Creating Dido Data](#creating-dido-data)
- [Creating Data From a Schema](#creating-data-from-a-schema)
- [Complex Schemas](#complex-schemas)
- [The Schema of a Schema](#the-schema-of-a-schema)
- [Schemas in Oddjob](#schemas-in-oddjob)

### Overview

Data is accessible via an
index or a field name. Here's an example of `DidoData` read from JSON:
```java
        DidoData data = DataInJson.with()
                .partialSchema(DataSchema.builder()
                        .addNamed("Qty", int.class)
                        .build())
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

### Data Schemas

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
and a type. No constraints are put on the type of data. Nested `DidoData` and 
repeating nested `DidoData` are also supported, although to what extent these 
are implemented by the formatters is limited. For instance, there is currently no way to
return a nested `DidoData` field from the column of a CSV file. 

### Instances of Dido Data

An instance of `DidoData` is often a wrapper over the underlying data read from some source. That which
wraps a `ResultSet` returned from a SQL query just passes all accessor methods through to the underlying
implementation. This is to allow a simple single threaded copy, such as out to CSV, to be as 
performant as possible. This wrapper instance in not immutable. Data should be copied to an immutable type
before publication to another thread.

Immutable and implementations of `DidoData` exist and include
[MapData](http://rgordon.co.uk/projects/dido/current/api/dido/data/MapData.html) and
[ArrayData](http://rgordon.co.uk/projects/dido/current/api/dido/data/ArrayData.html)
amongst others.

### Creating Dido Data

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

### Creating Data from a Schema

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
        DidoData data = DidoData.withSchema(schema)
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


### Complex Schemas

A schema can reference other schemas including themselves. It
does this using Defs and Refs. Here's an example of a tree:
```java
        DataSchema nodeSchema = DataSchema.builder()
                .withSchemaDefs(SchemaDefs.newInstance())
                .withSchemaName("Node")
                .addNamed("Name", String.class)
                .addRepeatingRefNamed("Children", "Node")
                .build();

        DidoData aTree = DidoData.withSchema(nodeSchema)
                .of("root", RepeatingData.of(
                        DidoData.withSchema(nodeSchema)
                                .of("child-a"),
                        DidoData.withSchema(nodeSchema)
                                .of("child-b", RepeatingData.of(
                                        DidoData.withSchema(nodeSchema).of("grandchild-1")))
                ));

        assertThat(aTree.toString(), ignoresAllWhitespaces(
                """
            {[1:Name]=root, [2:Children]=[
                {[1:Name]=child-a, [2:Children]=null},
                {[1:Name]=child-b, [2:Children]=[
                    {[1:Name]=grandchild-1, [2:Children]=null}]}]}
            """
        ));
```


### The Schema of a Schema

A schema can be converted to DidoData using `DataSchemaSchema` class.
Here's the above schema converted to DidoData.
```java
        DidoData schemaAsData = DataSchemaSchema.schemaToData(nodeSchema);

        assertThat(schemaAsData.toString(), ignoresAllWhitespaces(
                """
            {[1:Name]=Node, [2:Defs]=null, [3:Schema]={
                [1:Fields]=[
                    {[1:Index]=1, [2:Name]=Name, [3:Type]=java.lang.String, [4:Nested]=null},
                    {[1:Index]=2, [2:Name]=Children, [3:Type]=dido.data.RepeatingData, [4:Nested]={
                        [1:Ref]=Node, [2:Schema]=null}
                    }
                ]}
            }
            """
```


This data can then be serialized with anything that can serialize nested Dido Data. Here it is using 
the `dido-json` module.
```java
        StringWriter output = new StringWriter();

        try (DataOut out = DataOutJson.with()
                .schema(schemaAsData.getSchema())
                .pretty()
                .toAppendable(output)) {

            out.accept(schemaAsData);
        }

        assertThat(output.toString(), ignoresAllWhitespaces(
                """
{
  "Name": "Node",
  "Schema": {
    "Fields": [
      {
        "Index": 1,
        "Name": "Name",
        "Type": "java.lang.String"
      },
      {
        "Index": 2,
        "Name": "Children",
        "Type": "dido.data.RepeatingData",
        "Nested": {
          "Ref": "Node"
        }
      }
    ]
  }
}
                        """));
```


You'll notice that we use the 'Schema of a Schema' to create the Json. 

It's actually quite a complicated schema. Here's what it looks like as JSON:
```
{
  "Defs": [
    {
      "Def": "DataSchema",
      "Schema": {
        "Fields": [
          {
            "Index": 1,
            "Name": "Fields",
            "Type": "dido.data.RepeatingData",
            "Nested": {
              "Ref": "FieldSchema"
            }
          }
        ]
      }
    },
    {
      "Def": "FieldSchema",
      "Schema": {
        "Fields": [
          {
            "Index": 1,
            "Name": "Index",
            "Type": "int"
          },
          {
            "Index": 2,
            "Name": "Name",
            "Type": "java.lang.String"
          },
          {
            "Index": 3,
            "Name": "Type",
            "Type": "java.lang.String"
          },
          {
            "Index": 4,
            "Name": "Nested",
            "Type": "dido.data.DidoData",
            "Nested": {
              "Schema": {
                "Fields": [
                  {
                    "Index": 1,
                    "Name": "Ref",
                    "Type": "java.lang.String"
                  },
                  {
                    "Index": 2,
                    "Name": "Schema",
                    "Type": "dido.data.DidoData",
                    "Nested": {
                      "Ref": "DataSchema"
                    }
                  }
                ]
              }
            }
          }
        ]
      }
    }
  ],
  "Schema": {
    "Fields": [
      {
        "Index": 1,
        "Name": "Name",
        "Type": "java.lang.String"
      },
      {
        "Index": 2,
        "Name": "Defs",
        "Type": "dido.data.RepeatingData",
        "Nested": {
          "Schema": {
            "Fields": [
              {
                "Index": 1,
                "Name": "Def",
                "Type": "java.lang.String"
              },
              {
                "Index": 2,
                "Name": "Schema",
                "Type": "dido.data.DidoData",
                "Nested": {
                  "Ref": "DataSchema"
                }
              }
            ]
          }
        }
      },
      {
        "Index": 3,
        "Name": "Schema",
        "Type": "dido.data.DidoData",
        "Nested": {
          "Ref": "DataSchema"
        }
      }
    ]
  }
}
```


### Schemas in Oddjob

Oddjob has several types for creating and manipulating schemas. A good starting point is 
[dido:schema](https://github.com/robjg/dido/blob/master/docs/reference/dido/oddjob/schema/SchemaBean.md)
