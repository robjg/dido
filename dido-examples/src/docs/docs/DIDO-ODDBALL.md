Dido Oddball
============

Dido was initially created to provide Oddjob with the capability to copy and compare data 
from different sources. It was not until later that it was refactored to provide a fluent
API for doing the same in code that you see in the [README](../README.md).

The most likely entry point into Oddjob's world is the poorly documented [BeanBus](https://github.com/robjg/oddjob/blob/master/docs/reference/org/oddjob/beanbus/bus/BasicBusService.md)
component. This creates a pipeline that uses a *Bus Driver* to push data to a *Destination*

Dido's Bus Driver is [dido:data-in](reference/dido/oddjob/beanbus/DataInDriver.md) 
and its Destination is [dido:data-out](reference/dido/oddjob/beanbus/DataOutDestination.md)

Here's the simple CSV to JSON from the README just having run in Oddjob.

![Csv to Json in Oddjob](images/OddjobCsvJson.jpg)

This is the configuration it used:
{@oddjob.xml.file src/test/resources/examples/CsvToJson.xml}
And this is how to run it directly from code via Oddjob:
{@oddjob.java.file src/test/java/dido/examples/OddballExamplesTest.java#oddjobCode}
Here's the second example from the README configured for Oddjob. This is where we 
specify a schema.  
{@oddjob.xml.file src/test/resources/examples/CsvToJsonWithSchema.xml}

For more on how to configure the Dido components in Oddjob the best place to start
is the [Reference](reference/README.md)
