[HOME](../../README.md)
# dido:play

Plays back Data. Data is expected to be from three inputs for
data, schema and time such as those recorded with [dido:record](../../dido/replay/DataRecorderService.md).

### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) | The number of data items played. | 
| [dataIn](#propertydatain) | Override where the data will be sourced from. | 
| [dir](#propertydir) | Directory where the files are to be found. | 
| [filesPrefix](#propertyfilesprefix) | Optional file name prefix. | 
| [fromTime](#propertyfromtime) | If specified the player will skip forward to this time or after. | 
| [lastTime](#propertylasttime) | The timestamp of the last data item played. | 
| [name](#propertyname) | The name of the component. | 
| [playBackSpeed](#propertyplaybackspeed) | Allows time to be speeded up. | 
| [schemaIn](#propertyschemain) | Override where the schema will be sourced from. | 
| [timeIn](#propertytimein) | Override where the time will be sourced from. | 
| [to](#propertyto) | Where the data will be sent to. | 
| [toTime](#propertytotime) | If specified the player will stop replaying after this time. | 
| [wait](#propertywait) | The number of milliseconds until the next item is played. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Plays back data. |


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The number of data items played.

#### dataIn <a name="propertydatain"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the data will be sourced from.

#### dir <a name="propertydir"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Directory where the files are to be found.

#### filesPrefix <a name="propertyfilesprefix"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Optional file name prefix.

#### fromTime <a name="propertyfromtime"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

If specified the player will skip forward to
this time or after.

#### lastTime <a name="propertylasttime"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The timestamp of the last data item played.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component.

#### playBackSpeed <a name="propertyplaybackspeed"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to 1.0.</td></tr>
</table>

Allows time to be speeded up.

#### schemaIn <a name="propertyschemain"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the schema will be sourced from.

#### timeIn <a name="propertytimein"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Override where the time will be sourced from.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Automatically set in BeanBus.</td></tr>
</table>

Where the data will be sent to.

#### toTime <a name="propertytotime"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

If specified the player will stop replaying
after this time.

#### wait <a name="propertywait"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The number of milliseconds until the next item is played.


### Examples
#### Example 1 <a name="example1"></a>

Plays back data.
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
