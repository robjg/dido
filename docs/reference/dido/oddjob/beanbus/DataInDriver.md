[HOME](../../../README.md)
# dido:data-in

A Bean Bus Driver that reads data from [dido:data-in](../../../dido/oddjob/beanbus/DataInDriver.md) according to the
given [dido:data-in](../../../dido/oddjob/beanbus/DataInDriver.md) and forward the `dido.data.GenericData` to the next component.
See any of the formatters for examples of how to use.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) | Count of data items read in. | 
| [from](#propertyfrom) | Where to read data from. | 
| [how](#propertyhow) | How to read the data in. | 
| [name](#propertyname) | The name of the component. | 
| [stop](#propertystop) | Internal stop flag. | 
| [to](#propertyto) | If set, data will be forwarded here. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Read lines in. |


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Count of data items read in.

#### from <a name="propertyfrom"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

Where to read data from.

#### how <a name="propertyhow"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

How to read the data in.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component.

#### stop <a name="propertystop"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Internal stop flag. Set by calling stop.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, but fairly pointless if missing.</td></tr>
</table>

If set, data will be forwarded here. Set automatically by BeanBus.


### Examples
#### Example 1 <a name="example1"></a>

Read lines in.
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
                                <bean class="dido.oddjob.stream.StreamLines$In"/>
                            </how>
                        </dido:data-in>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <to>
                                <value value="${vars.results}"/>
                            </to>
                            <how>
                                <bean class="dido.oddjob.stream.StreamLines$Out"/>
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
