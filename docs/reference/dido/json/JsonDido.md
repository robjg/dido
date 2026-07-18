[HOME](../../README.md)
# dido:json

Creates an In or an Out for JSON data. Data can either be in the format
of a single JSON Object per line. An array of JSON Objects, or A single JSON Object.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [classLoader](#propertyclassloader) | The class loader used to create the types for the specified dido conversions. | 
| [conversionProvider](#propertyconversionprovider) | A Conversion Provider used when Dido conversions are specified. | 
| [didoConversion](#propertydidoconversion) | Specify a Dido Conversion is to be used for the given transformation pair of types. | 
| [format](#propertyformat) | The format of the data. | 
| [gsonBuilder](#propertygsonbuilder) | Configure the Gson Builder directly. | 
| [objectToNumberPolicy](#propertyobjecttonumberpolicy) | Configures Gson to apply a specific number strategy during deserialization of number type primitives. | 
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
| [Example 4](#example4) | Configuring the Gson Builder directly using JavaScript. |


### Property Detail
#### classLoader <a name="propertyclassloader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Set automatically by the framework.</td></tr>
</table>

The class loader used to create the types for the specified
dido conversions.

#### conversionProvider <a name="propertyconversionprovider"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A Conversion Provider used when Dido conversions are specified.

#### didoConversion <a name="propertydidoconversion"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify a Dido Conversion is to be used for the given
transformation pair of types. When reading in the key is given as the
type that Gson will provide from the JSON element, Typically, this will be
String, Double, Boolean, or Map. The value is the type Dido
will convert to, and will be the more complicated type. When writing
Data out, the key is the complex Dido type and the value is the simpler Gson
aware type.

#### format <a name="propertyformat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to LINES.</td></tr>
</table>

The format of the data. LINES, ARRAY, SINGLE.

#### gsonBuilder <a name="propertygsonbuilder"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Configure the Gson Builder directly. This property specifies any number of Consumers of
the Gson Builder. See the examples for using this with JavaScript.

#### objectToNumberPolicy <a name="propertyobjecttonumberpolicy"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults numbers as doubles.</td></tr>
</table>

Configures Gson to apply a specific number strategy during deserialization of
number type primitives. This is what will be used for a partial or no schema when converting numbers.

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
      <tr><td><i>Required</i></td><td>No, defaults to LEGACY_STRICT.</td></tr>
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

The output in results is:
```
[
  { "Fruit":"Apple", "Qty":5, "Price":27.2 },
  { "Fruit":"Orange", "Qty":10, "Price":31.6 },
  { "Fruit":"Pear", "Qty":7, "Price":22.1 }
]
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
                                <buffer><![CDATA[{ "Fruit":"Apple", "Price":Infinity }
{ "Fruit":null, "Price":31.6 }
{ "Fruit":"Pear", "Price":NaN }
]]></buffer>
                            </from>
                        </dido:data-in>
                        <bus:collect id="capture"/>
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


#### Example 4 <a name="example4"></a>

Configuring the Gson Builder directly using JavaScript.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <file file="${java.io.tmpdir}" key="work.dir"/>
                    </values>
                </properties>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json format="LINES">
                                    <gsonBuilder>
                                        <value value="#{new (Java.extend(Java.type('java.util.function.Consumer'), { accept: function(gson) { gson.setObjectToNumberStrategy(Java.type('com.google.gson.ToNumberPolicy').LONG_OR_DOUBLE); } } ))() }"/>
                                    </gsonBuilder>
                                </dido:json>
                            </how>
                            <from>
                                <buffer><![CDATA[{ "Fruit":"Apple", "Qty":5, "Price":24.5  }
{ "Fruit":"Pear", "Qty":3, "Price":35.5 }
]]></buffer>
                            </from>
                        </dido:data-in>
                        <bus:bus name="Wire Tap">
                            <of>
                                <bus:collect>
                                    <output>
                                        <file file="${work.dir}/FromToWithGsonBuilderData.txt"/>
                                    </output>
                                </bus:collect>
                            </of>
                        </bus:bus>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json>
                                    <gsonBuilder>
                                        <value value="#{new (Java.extend(Java.type('java.util.function.Consumer'), { accept: function(gson) { gson.setFieldNamingStrategy(Java.type('com.google.gson.FieldNamingPolicy').UPPER_CASE_WITH_UNDERSCORES); } } ))() }"/>
                                    </gsonBuilder>
                                </dido:json>
                            </how>
                            <to>
                                <file file="${work.dir}/FromToWithGsonBuilderOut.json"/>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```

The captured data is:
```
{[1:Fruit]=Apple, [2:Qty]=5, [3:Price]=24.5}
{[1:Fruit]=Pear, [2:Qty]=3, [3:Price]=35.5}
```

The output Json is:
```
{"Fruit":"Apple","Qty":5,"Price":24.5}
{"Fruit":"Pear","Qty":3,"Price":35.5}
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
