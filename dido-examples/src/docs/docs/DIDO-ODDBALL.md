Dido Oddball
============

- [Overview](#overview)
- [An Example](#an-example)
- [Running](#running)
- [A Second Example](#a-second-example)
- [Further Information](#further-information)

### Overview

Dido was initially created to provide Oddjob with the capability to copy and compare data 
from different sources. It was not until later that it was refactored to provide a fluent
API for doing the same in code that you see in the [README](../README.md).

The most likely entry point into Oddjob's world is the poorly documented [BeanBus](https://github.com/robjg/oddjob/blob/master/docs/reference/org/oddjob/beanbus/bus/BasicBusService.md)
component. This creates a pipeline that uses a *Bus Driver* to push data to a *Destination*

Dido's Bus Driver is [dido:data-in](reference/dido/oddjob/beanbus/DataInDriver.md) 
and its Destination is [dido:data-out](reference/dido/oddjob/beanbus/DataOutDestination.md)

### An Example

Here's the simple CSV to JSON from the README just having run in Oddjob.

![Csv to Json in Oddjob](images/OddjobCsvJson.jpg)

This is the configuration it used:
{@oddjob.xml.file src/test/resources/examples/CsvToJson.xml}

### Running

And this is how to run it directly from code via Oddjob:
{@oddjob.java.file src/test/java/dido/examples/OddballExamplesTest.java#oddjobCode}
The dependencies for this example were resolved using Maven.
Look at [dido-examples/pom.xml](../dido-examples/pom.xml) for what was required.
If you use the Downloaded Oddjob Application then Dido can be resolved 
as an 'Oddball'. Here is the configuration to enable the example to be run in this
way:
{@oddjob.xml.file src/test/resources/examples/DidoResolve.xml}
Obviously this complicates the configuration somewhat. It would be
quite simple to create an Oddjob that copied the resolved files into Oddjob's Oddball 
directory so that Dido was permanently available within Oddjob. 

### A Second Example

Here's the second example from the README configured for Oddjob. This is where we 
specify a schema.  
{@oddjob.xml.file src/test/resources/examples/CsvToJsonWithSchema.xml}

### Further Information

For more on how to configure the Dido components in Oddjob the best place to start
is the [Reference](reference/README.md)
