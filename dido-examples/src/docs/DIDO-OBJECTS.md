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
We can also serialize a Java Stream of beans:
{@oddjob.java.file src/test/java/dido/examples/ObjectExamplesTest.java#serializeStream}


