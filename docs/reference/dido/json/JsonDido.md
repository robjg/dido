[HOME](../../README.md)
# dido:json

Creates an In or an Out for JSON data. Data can either be in the format
of a single JSON Object per line. An array of JSON Objects, or A single JSON Object.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [format](#propertyformat) | The format of the data. | 
| [partialSchema](#propertypartialschema) | When reading data in, indicates that the provided Schema is partial. | 
| [schema](#propertyschema) | The schema to use. | 
| [serializeNulls](#propertyserializenulls) | Serialize null values. | 
| [serializeSpecialFloatingPointValues](#propertyserializespecialfloatingpointvalues) | Serialize NaN and Infinity values. | 
| [strictness](#propertystrictness) | Gson Strictness passed through to underlying Gson builder. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From JSON Lines and back again. |
| [Example 2](#example2) | From JSON Array and back again. |
| [Example 3](#example3) | Json with Nulls and Special Floating Point Numbers. |


### Property Detail
#### format <a name="propertyformat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to LINES.</td></tr>
</table>

The format of the data. LINES, ARRAY, SINGLE.

#### partialSchema <a name="propertypartialschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

When reading data in, indicates that the provided Schema is partial. The
rest of the schema will be taken from the data.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schema to use. When reading in, if one is not provided a simple schema will be
created based on the JSON primitive type. When writing out the schema will be used to limit the number
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
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Gson Strictness passed through to underlying Gson builder.


### Examples
#### Example 1 <a name="example1"></a>

From JSON Lines and back again.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <dido:data-in xmlns:dido="oddjob:dido">
                    <how>
                        <dido:json/>
                    </how>
                    <from>
                        <buffer>
                            <![CDATA[{ "Fruit":"Apple", "Qty":5, "Price":27.2 }
{ "Fruit":"Orange", "Qty":10, "Price":31.6 }
{ "Fruit":"Pear", "Qty":7, "Price":22.1 }
]]>
                        </buffer>
                    </from>
                </dido:data-in>
                <dido:data-out xmlns:dido="oddjob:dido">
                    <how>
                        <dido:json/>
                    </how>
                    <to>
                        <identify id="results">
                            <value>
                                <buffer/>
                            </value>
                        </identify>
                    </to>
                </dido:data-out>
            </of>
        </bus:bus>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

From JSON Array and back again.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <dido:data-in xmlns:dido="oddjob:dido">
                    <how>
                        <dido:json format="ARRAY"/>
                    </how>
                    <from>
                        <buffer>
                            <![CDATA[
[
    { "Fruit":"Apple", "Qty":5, "Price":27.2 },
    { "Fruit":"Orange", "Qty":10, "Price":31.6 },
    { "Fruit":"Pear", "Qty":7, "Price":22.1 }
]
]]>

                        </buffer>
                    </from>
                </dido:data-in>
                <dido:data-out xmlns:dido="oddjob:dido">
                    <how>
                        <dido:json format="ARRAY"/>
                    </how>
                    <to>
                        <identify id="results">
                            <value>
                                <buffer/>
                            </value>
                        </identify>
                    </to>
                </dido:data-out>
            </of>
        </bus:bus>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Json with Nulls and Special Floating Point Numbers. Without setting the properties
the jobs would fail.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json strictness="LENIENT">
                                    <schema>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="Fruit" type="java.lang.String"/>
                                                <dido:field name="Price" type="double"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                </dido:json>
                            </how>
                            <from>
                                <buffer>
                                    <![CDATA[{ "Fruit":"Apple", "Price":Infinity }
{ "Fruit":null, "Price":31.6 }
{ "Fruit":"Pear", "Price":NaN }
]]>
                                </buffer>
                            </from>
                        </dido:data-in>
                        <bus:bus xmlns:bus="oddjob:beanbus">
                            <of>
                                <bus:collect id="capture"/>
                            </of>
                        </bus:bus>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json serializeNulls="true" serializeSpecialFloatingPointValues="true"/>
                            </how>
                            <to>
                                <identify id="results">
                                    <value>
                                        <buffer/>
                                    </value>
                                </identify>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
                <echo><![CDATA[${results}]]></echo>
            </jobs>
        </sequential>
    </job>
</oddjob>
```

The captured data is:
```
{[1:Fruit]=Apple, [2:Price]=Infinity}
{[1:Fruit]=null, [2:Price]=31.6}
{[1:Fruit]=Pear, [2:Price]=NaN}
```

The output in results is:
```
{"Fruit":"Apple","Price":Infinity}
{"Fruit":null,"Price":31.6}
{"Fruit":"Pear","Price":NaN}
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
