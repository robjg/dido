[HOME](../../README.md)
# dido:to-json

Provides a Mapping Function that will convert a Dido Data
into a JSON String. See also [dido:from-json](../../dido/json/FromJsonStringType.md)

### Property Summary

| Property | Description |
| -------- | ----------- |
| [schema](#propertyschema) | The schema to use. | 
| [serializeNulls](#propertyserializenulls) | Serialize null values. | 
| [serializeSpecialFloatingPointValues](#propertyserializespecialfloatingpointvalues) | Serialize NaN and Infinity values. | 
| [strictness](#propertystrictness) | Gson Strictness passed through to underlying Gson builder. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From JSON Strings using a Mapping function and back again. |


### Property Detail
#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schema to use. This schema will be used to limit the number
of fields written.

#### serializeNulls <a name="propertyserializenulls"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Serialize null values. True to serialize null to the JSON,
false and they will be ignored and no field will be written.

#### serializeSpecialFloatingPointValues <a name="propertyserializespecialfloatingpointvalues"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Serialize NaN and Infinity values. True to serialize, false
and these values in data will result in an Exception. Note that because of an
oversight in the underlying Gson implementation, this has the same effect as
setting Strictness to LENIENT.

#### strictness <a name="propertystrictness"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to Gson default, LEGACY_STRICT.</td></tr>
</table>

Gson Strictness passed through to underlying Gson builder.


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
                        <buffer><![CDATA[{ "Fruit":"Apple", "Qty":5, "Price":27.2 }
{ "Fruit":"Orange", "Qty":10, "Price":31.6 }
{ "Fruit":"Pear", "Qty":7, "Price":22.1 }
]]></buffer>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:from-json objectToNumberPolicy="LONG_OR_DOUBLE" xmlns:dido="oddjob:dido"/>
                    </function>
                </bus:map>
                <bus:map>
                    <function>
                        <dido:to-json xmlns:dido="oddjob:dido"/>
                    </function>
                    <to>
                        <stdout/>
                    </to>
                </bus:map>
            </of>
        </bus:bus>
    </job>
</oddjob>
```

The output Json is:
```
{"Fruit":"Apple","Qty":5,"Price":27.2}
{"Fruit":"Orange","Qty":10,"Price":31.6}
{"Fruit":"Pear","Qty":7,"Price":22.1}
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
