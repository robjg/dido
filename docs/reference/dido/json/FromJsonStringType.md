[HOME](../../README.md)
# dido:from-json

Provides a Mapping Function that will convert a GSON String
into Dido Data.


See also [dido:to-json](../../dido/json/ToJsonStringType.md)

### Property Summary

| Property | Description |
| -------- | ----------- |
| [partialSchema](#propertypartialSchema) | Indicates that the provided Schema is partial. | 
| [schema](#propertyschema) | The schema to use. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From JSON Strings using a Mapping function and back again. |


### Property Detail
#### partialSchema <a name="propertypartialSchema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Indicates that the provided Schema is partial. The
rest of the schema will be taken from the data.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schema to use. If one is not provided a simple schema will be
created based on the JSON primitive type.


### Examples
#### Example 1 <a name="example1"></a>

From JSON Strings using a Mapping function and back again.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <buffer>
                            <![CDATA[{ "Fruit"="Apple", "Qty"=5, "Price"=27.2 }
{ "Fruit"="Orange", "Qty"=10, "Price"=31.6 }
{ "Fruit"="Pear", "Qty"=7, "Price"=22.1 }
]]>
                        </buffer>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:from-json xmlns:dido="oddjob:dido"/>
                    </function>
                </bus:map>
                <bus:map>
                    <function>
                        <dido:to-json xmlns:dido="oddjob:dido"/>
                    </function>
                    <to>
                        <identify id="results">
                            <value>
                                <buffer/>
                            </value>
                        </identify>
                    </to>
                </bus:map>
            </of>
        </bus:bus>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
