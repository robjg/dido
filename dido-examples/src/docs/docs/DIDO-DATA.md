Dido Data
=========

[DidoData](http://rgordon.co.uk/projects/dido/current/api/dido/data/DidoData.html) 
defines the data format common to all sources of data. 

- [Overview](#overview)
- [Data Schemas](#data-schemas)
- [Instances](#instances-of-dido-data)
- [Creating Dido Data](#creating-dido-data)
- [Creating Data From a Schema](#creating-data-from-a-schema)

### Overview

Data is accessible via an
index or a field name. Here's an example of `DidoData` read from JSON:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet6}
Non-boxing access is available for all primitive types, and String is added as a convenience.
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet7}
Use of boxed types is implementation specific, so using the primitive accessors may
not always yield a performance improvement. 

### Data Schemas

`DidoData` always has a [DataSchema](http://rgordon.co.uk/projects/dido/current/api/dido/data/DataSchema.html) that
defines its layout.
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet8}
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
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet1}
Or using a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet2}
(Note the none-boxed types here)

### Creating Data from a Schema

A `DataSchema` can also be created with a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet3}
And now we can create data matching the schema from field values:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet4}
Or a builder:
{@oddjob.java.file src/test/java/dido/examples/DidoDataExamplesTest.java#snippet5}


### Concatenating Data

### Sub Data

### Nested Data

### Schema Manager
