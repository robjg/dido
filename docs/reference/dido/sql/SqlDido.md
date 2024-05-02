[HOME](../../README.md)
# dido:sql

Export and Import with SQL.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [batchSize](#propertybatchSize) | Batch size. | 
| [classLoader](#propertyclassLoader) | The classloader used to derive the schema from a query. | 
| [inType](#propertyinType) |  | 
| [outType](#propertyoutType) |  | 
| [sql](#propertysql) | The SQL that will extract or insert the data. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Insert and Extract data. |


### Property Detail
#### batchSize <a name="propertybatchSize"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Batch size. This is passed directly to JDBC.

#### classLoader <a name="propertyclassLoader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The classloader used to derive the schema from a query. Only required if the
table include custom data types.

#### inType <a name="propertyinType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>



#### outType <a name="propertyoutType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>



#### sql <a name="propertysql"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The SQL that will extract or insert the data.


### Examples
#### Example 1 <a name="example1"></a>

Insert and Extract data.


_java.io.IOException: No Resource Found: 'dido/sql/SimpleSqlExample.xml', classloader=java.net.URLClassLoader@3612f691_


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
