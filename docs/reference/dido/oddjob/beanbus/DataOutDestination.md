[HOME](../../../README.md)
# dido:data-out

A Bean Bus Destination that accepts `dido.data.DidoData` and writes it out to the given
'to' according to the given 'how'.
See any of the formatters for examples of how to use.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) | Count of data sent out. | 
| [how](#propertyhow) | How to write the data out. | 
| [name](#propertyname) | The name of the component. | 
| [next](#propertynext) | If set, data will be forwarded here. | 
| [to](#propertyto) | Where to write data to. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Writes lines out. |


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Count of data sent out.

#### how <a name="propertyhow"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

How to write the data out.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component.

#### next <a name="propertynext"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

If set, data will be forwarded here. Set automatically by BeanBus.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

Where to write data to.


### Examples
#### Example 1 <a name="example1"></a>

Writes lines out.
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
                        <dido:data-in xmlns:dido="oddjob:dido">
                            <from>
                                <buffer>
                                    <![CDATA[The
Quick
Brown
Fox
Jumped
Over
The
Lazy
Dog]]>
                                </buffer>
                            </from>
                            <how>
                                <dido:lines/>
                            </how>
                        </dido:data-in>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <to>
                                <value value="${vars.results}"/>
                            </to>
                            <how>
                                <dido:lines/>
                            </how>
                        </dido:data-out>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
