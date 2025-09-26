[HOME](../../README.md)
# dido:table

Creates an Out that write data to a text table.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [borderStyle](#propertyborderstyle) | A string code for which borders to show. | 
| [schema](#propertyschema) | The schema to use when writing out the schema will be used to limit the number of fields written. | 
| [shownBorders](#propertyshownborders) | A string code for which borders to show. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | To a Text Table. |


### Property Detail
#### borderStyle <a name="propertyborderstyle"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A string code for which borders to show. Until this is documented here, you'll need
to look at the source code in Maven to understand the format.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schema to use when writing out the schema will be used to limit the number
of fields written.

#### shownBorders <a name="propertyshownborders"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A string code for which borders to show. Until this is documented here, you'll need
to look at the source code in Maven to understand the format.


### Examples
#### Example 1 <a name="example1"></a>

To a Text Table.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver xmlns:bus="oddjob:beanbus">
                    <values>
                        <bean class="dido.text.SomeData"/>
                    </values>
                </bus:driver>
                <dido:data-out xmlns:dido="oddjob:dido">
                    <how>
                        <dido:table/>
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
