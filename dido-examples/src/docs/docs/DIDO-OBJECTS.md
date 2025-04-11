Dido Object
===========

Serialize Java Objects to Dido Data, and deserialize Dido Data
to Java Objects.

Serializing
-----------

Given an Object with standard Java Getters:
{@oddjob.java.file src/test/java/dido/examples/objects/Apple.java}
We can serialize this to DidoData:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#serializeSingle}
Java doesn't give us any control over the order methods are
provided so we don't know the order of the fields in the data,
we can only access the data by name. We can solve this
problem by providing the properties in the order we want in the data:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#serializeFields}

We can also serialize a Java Collection of beans using a `DataIn` in a similar way
to other formatters:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#dataIn}
However in most situations using the `mapper()` method will be more natural:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#serializeStream}

Deserializing
-----------

Given an Object with standard Java Setters:
{@oddjob.java.file src/test/java/dido/examples/objects/FruitBean.java}
We can create this from DidoData:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#deserializeSingle}
The data must have field names that match the properties we want to set,
that's why we used the builder here to create the DidoData.

We can also consume a Collection of DidoData as Objects in a `DataOut`:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#deserializeList}
Providing the schema as we have done here speeds things up slightly
as the transformation can be calculated once up front and
not done on the fly from the data as it would otherwise have to be.

This chained consumer pipeline is useful for an asynchronous subscription
scenario, however again a `mapper()` is probably more useful:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#deserializeStream}
Which achieves the same as the above.
