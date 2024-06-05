[HOME](../../../README.md)
# dido:set

Set the value for a field or index. Participates in an [dido:transform](../../../dido/oddjob/transform/Transform.md). If
no field or index is specified the index is taken by the position in the transform.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [field](#propertyfield) | The field name. | 
| [index](#propertyindex) | The index. | 
| [type](#propertytype) | The type. | 
| [value](#propertyvalue) | The value. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Set a value. |


### Property Detail
#### field <a name="propertyfield"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

The field name.

#### index <a name="propertyindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

The index.

#### type <a name="propertytype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to that from the existing schema.</td></tr>
</table>

The type. A conversion will be attempted from the value to this type.
This type will also be used for a new schema field.

#### value <a name="propertyvalue"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. If not specified null will be attempted to be set on the field.</td></tr>
</table>

The value.


### Examples
#### Example 1 <a name="example1"></a>

Set a value.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <list>
                            <values>
                                <bean class="dido.oddjob.bean.FruitData" price="27.2" qty="5" type="Apple"/>
                                <bean class="dido.oddjob.bean.FruitData" price="31.6" qty="10" type="Orange"/>
                                <bean class="dido.oddjob.bean.FruitData" price="22.1" qty="7" type="Pear"/>
                            </values>
                        </list>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:transform xmlns:dido="oddjob:dido">
                            <of>
                                <dido:set field="qty">
                                    <value>
                                        <value value="20"/>
                                    </value>
                                </dido:set>
                            </of>
                        </dido:transform>
                    </function>
                </bus:map>
                <bus:collect id="capture"/>
            </of>
        </bus:bus>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
