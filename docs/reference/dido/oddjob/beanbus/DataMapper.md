[HOME](../../../README.md)
# dido:map

Provide a BeanBus component that will work specifically
with Schema Aware Functions to initialise them and propagate any resultant
schema to the next component if it is Schema Aware.


This is functionally equivalent to `bus:map` but providing access to schemas
may be beneficial as mappers don't need to wait for data for a schema to initialise
with.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) | The number of items provided to the function. | 
| [exceptionListener](#propertyexceptionlistener) |  | 
| [function](#propertyfunction) | The function to apply to data on the bus. | 
| [name](#propertyname) | The name of the component as seen in Oddjob. | 
| [schema](#propertyschema) | The resultant schema if the function is able to provide it. | 
| [sent](#propertysent) | The number of items returned by the function. | 
| [to](#propertyto) | The next component in the bus. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From and To lines of text. |


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The number of items provided to the function.

#### exceptionListener <a name="propertyexceptionlistener"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>



#### function <a name="propertyfunction"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The function to apply to data on the bus.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component as seen in Oddjob.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The resultant schema if the function is able to provide it.

#### sent <a name="propertysent"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The number of items returned by the function.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The next component in the bus.


### Examples
#### Example 1 <a name="example1"></a>

From and To lines of text.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <results>
                        <buffer/>
                    </results>
                </variables>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <copy>
                            <input>
                                <buffer><![CDATA[The
Quick
Brown
Fox
Jumped
Over
The
Lazy
Dog]]></buffer>
                            </input>
                        </copy>
                        <dido:map id="map-in" xmlns:dido="oddjob:dido">
                            <function>
                                <dido:lines-in fieldName="stuff" xmlns:dido="oddjob:dido"/>
                            </function>
                        </dido:map>
                        <dido:map id="map-out" xmlns:dido="oddjob:dido">
                            <function>
                                <dido:lines-out fieldName="stuff" xmlns:dido="oddjob:dido"/>
                            </function>
                        </dido:map>
                        <bus:collect>
                            <output>
                                <value value="${vars.results}"/>
                            </output>
                        </bus:collect>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
