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
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetGiven}
We can write a standard java function to map it to some new data:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetBuilder}
This is fine, but will be slow. For each data item the schema is recreated,
and field name access may not be the quickest way of accessing the underlying
data. 

### Field Transforms - As A View or Copy

If we know the schema in advance, we can use `FieldViews` and an `ViewTransformBuilder` to do the same thing:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#viewTransform}
The resultant data is a view of the underlying data. This is often the most performant way of reading data, 
changing a few things, and writing it out again. However, if the data is read more than once, discounting our 
price will be done with each read, and if our view is of mutable data (such as that from a SQL query) and
is not immediately written out then we need to copy the data during the transformation.

This can be achieved using `WriteTransformBuilder` in exactly the same way.
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#copyTransform}
In both instance the operations will find the quickest way of reading the data and in the latter,
of setting the data.
Even though we are creating the Transformation with field names, indexes might be
used for the underlying data access which has been observed to yield a 4 times performance improvement.

### Field Transforms - Resultant Schema

A DidoTransform is a java function that also provides a resultant schema. Providing 
this schema to a downstream Transformation or Data Out operation may well also 
improve performance.
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetResultSchema}

### Concatenating Data

We can concatenate data:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#concat}

### Removing Data

We can create subData. 
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#subData}
This creates a view on the data. No underlying data is changed. It may be 
slightly more performant than doing the same with field level removes but
this has not been proven.

### Oddjob

For examples of using Dido Operators in Oddjob, see [dido:transform](reference/dido/operators/transform/TransformationFactory.md)
