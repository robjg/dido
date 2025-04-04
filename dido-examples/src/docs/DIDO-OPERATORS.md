Dido Operators
==============

Operators exist in [dido-operators](dido-operators) for transforming 
data.

### Field Level Transforms

Given some data:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetGiven}
We can write a standard java function to map it to some new data:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetBuilder}
This is fine, but will be slow. For each data item the schema is recreated,
and field name access may not be the quickest way of accessing the underlying
data. 

If we know the schema in advance, we can use FieldOps in an OpTransformBuilder to do the same thing:
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippet1}
Because the field ops will find the quickest way of getting and setting the data,
even though we are creating the Transformation with field names, indexes might be
used for the underlying data access.

A DidoTransform is a java function that also provides a resultant schema. Providing 
this schema to a downstream Transformation or Data Out operation may well also 
improve performance.
{@oddjob.java.file src/test/java/dido/examples/FieldTransformExamplesTest.java#snippetResultSchema}

### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:transform](docs/reference/dido/operators/transform/TransformationFactory.md)
