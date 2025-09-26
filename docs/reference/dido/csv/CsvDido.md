[HOME](../../README.md)
# dido:csv

Reads and write CSV format from Dido Data.
<a href="https://commons.apache.org/proper/commons-csv/">Apache Commons CSV</a>
is used for this using the DEFAULT format. Please see their documentation
for more details.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [converter](#propertyconverter) | A converter used to convert Strings to the required schema type. | 
| [csvFormat](#propertycsvformat) | The CSV Format to use. | 
| [partialSchema](#propertypartialschema) | When reading data in, indicates that the provided Schema is partial. | 
| [schema](#propertyschema) | The schema to use. | 
| [withHeader](#propertywithheader) | Does the data contain a header or is a header to be written. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From CSV data and back again. |
| [Example 2](#example2) | Using a Custom Format. |


### Property Detail
#### converter <a name="propertyconverter"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

A converter used to convert Strings to the required schema type.
Note Converter is only used for Input. Output is more complicated as the printer needs to know
if values should be quoted, so we can't pre convert to a String.

#### csvFormat <a name="propertycsvformat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The CSV Format to use. See the <a href="https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html">CSVFormat JavaDoc</a>
for more information.

#### partialSchema <a name="propertypartialschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

When reading data in, indicates that the provided Schema is partial. The
rest of the schema will be taken from the header.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schema to use. When reading in, if one is not provided an all String schema will be
created. When writing out the schema is only used to provide a header in the event that no data arrives. It
is expected to match the schema of the data. No check is made to ensure it does.

#### withHeader <a name="propertywithheader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Does the data contain a header or is a header to be written.


### Examples
#### Example 1 <a name="example1"></a>

From CSV data and back again.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <dido:data-in xmlns:dido="oddjob:dido">
                    <how>
                        <dido:csv withHeader="true"/>
                    </how>
                    <from>
                        <buffer>
                            <![CDATA[Fruit,Qty,Price
Apple,5,27.2
Orange,10,31.6
Pear,7,22.1]]>
                        </buffer>
                    </from>
                </dido:data-in>
                <dido:data-out xmlns:dido="oddjob:dido">
                    <how>
                        <dido:csv withHeader="true"/>
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

Using a Custom Format.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <dido:data-in xmlns:dido="oddjob:dido">
                    <how>
                        <dido:csv withHeader="true">
                            <csvFormat>
                                <dido:csv-format delimiter="|" formatFrom="RFC4180" nullString="null"/>
                            </csvFormat>
                        </dido:csv>
                    </how>
                    <from>
                        <buffer><![CDATA[Fruit|Qty|Price
Apple|5|27.2
Orange|null|31.6
Pear|7|22.1]]></buffer>
                    </from>
                </dido:data-in>
                <dido:data-out xmlns:dido="oddjob:dido">
                    <how>
                        <dido:csv withHeader="true">
                            <csvFormat>
                                <dido:csv-format formatFrom="TDF" nullString="blank"/>
                            </csvFormat>
                        </dido:csv>
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



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
