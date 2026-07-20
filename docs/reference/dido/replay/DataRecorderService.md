[HOME](../../README.md)
# dido:record

Records data it receives to files. This recorder writes
three files simultaneously; one of data, one of schemas, and one of time stamps.
The data and schema are written as JSON lines. The timestamps are lines
of text written using ISO-8601 format. The default names of the files are
`data.jsonl, schema.jsonl, time.txt`. These name can be prefixed with
an optional property `filesPrefix`.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [clock](#propertyclock) | The clock to use for the timestamp. | 
| [count](#propertycount) | Count of items recorded. | 
| [dataOut](#propertydataout) | Override where the data will be written to. | 
| [dir](#propertydir) | Directory where the files will be created. | 
| [filesPrefix](#propertyfilesprefix) | Optional file name prefix. | 
| [name](#propertyname) | The name of the component. | 
| [schemaOut](#propertyschemaout) | Override where the schema will be written to. | 
| [timeOut](#propertytimeout) | Override where the time will be written to. | 
| [to](#propertyto) | An onward consumer of the data. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Records data. |


### Property Detail
#### clock <a name="propertyclock"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Default to system time.</td></tr>
</table>

The clock to use for the timestamp.

#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

Count of items recorded.

#### dataOut <a name="propertydataout"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the data will be written to.

#### dir <a name="propertydir"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Directory where the files will be created.

#### filesPrefix <a name="propertyfilesprefix"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Optional file name prefix.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component.

#### schemaOut <a name="propertyschemaout"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the schema will be written to.

#### timeOut <a name="propertytimeout"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the time will be written to.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

An onward consumer of the data.


### Examples
#### Example 1 <a name="example1"></a>

Records data.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <properties>
                    <values>
                        <file file="${java.io.tmpdir}" key="work.dir"/>
                        <value value="stuff-" key="replay.files.prefix"/>
                    </values>
                </properties>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in id="csv" xmlns:dido="oddjob:dido">
                            <how>
                                <dido:csv>
                                    <schema>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="type" type="java.lang.String"/>
                                                <dido:field name="quantity" type="int"/>
                                                <dido:field name="price" type="double"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                </dido:csv>
                            </how>
                            <from>
                                <buffer>
                                    <![CDATA[Apple,5,27.2
Orange,10,31.6
Pear,7,22.1]]>
                                </buffer>
                            </from>
                        </dido:data-in>
                        <dido:record filesPrefix="${replay.files.prefix}" xmlns:dido="oddjob:dido">
                            <dir>
                                <file file="${work.dir}"/>
                            </dir>
                        </dido:record>
                    </of>
                </bus:bus>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:play filesPrefix="${replay.files.prefix}" id="play" xmlns:dido="oddjob:dido">
                            <to>
                                <list/>
                            </to>
                            <dir>
                                <file file="${work.dir}"/>
                            </dir>
                        </dido:play>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
