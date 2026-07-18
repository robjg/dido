[HOME](../../../README.md)
# dido:to-dido

Provide a BeanBus component that uses a mapper to convert to
`dido.data.DidoData` from some other data type.



### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) |  | 
| [function](#propertyfunction) |  | 
| [name](#propertyname) |  | 
| [sent](#propertysent) |  | 
| [to](#propertyto) |  | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From and To lines of text. |


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>



#### function <a name="propertyfunction"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
</table>



#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>



#### sent <a name="propertysent"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>



#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
</table>




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
                    <how>
                        <dido:lines fieldName="stuff" xmlns:dido="oddjob:dido"/>
                    </how>
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
                        <dido:to-dido xmlns:dido="oddjob:dido">
                            <function>
                                <value value="${vars.how}"/>
                            </function>
                        </dido:to-dido>
                        <dido:from-dido xmlns:dido="oddjob:dido">
                            <function>
                                <value value="${vars.how}"/>
                            </function>
                        </dido:from-dido>
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
