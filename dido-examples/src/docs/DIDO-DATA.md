Dido Data
=========

[DidoData](http://rgordon.co.uk/projects/dido/current/api/dido/data/DidoData.html) 
defines the data format common to all sources of data. Data is accessible via an
index or a field name. Here's an example of `DidoData` read from JSON:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet6}
Non-boxing access is available for all primitive types.
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet7}
Use of boxed types is implementation specific, so using the primitive accessors may
not always yield a performance improvement. 

`DidoData` always has a [DataSchema](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataSchema.html) that
defines its layout.
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet8}
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

Convenience static creation methods exist on `DidoData` which will the implementation 
that is considered the most performant for general use in any release.

`DidoData` can be created from field values:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet1}
Or using a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet2}
(Note the none-boxed types here)

A `DataSchema` can also be created with a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet3}
And now we can create data matching the schema from field values:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet4}
Or a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet5}

Not all implementations of `DidoData` are immutable. That which
wraps a `ResultSet` returned from a SQL query is not. This is to allow
a simple single threaded copy to be as performant as possible. To ensure 
thread safety data should be copied to an immutable type.  
