[HOME](../../../README.md)
# dido:set

Set the value for a field or index. Participates in an [dido:transform](../../../dido/operators/transform/TransformationFactory.md). If
no field or index is specified the index is taken by the position in the transform.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [conversionProvider](#propertyconversionProvider) | A Conversion provider that will be used to convert the value to the type. | 
| [field](#propertyfield) | The field name. | 
| [type](#propertytype) | The type. | 
| [value](#propertyvalue) | The value. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Set a value. |


### Property Detail
#### conversionProvider <a name="propertyconversionProvider"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to a simple one.</td></tr>
</table>

A Conversion provider that will be used to convert the value to the type.

#### field <a name="propertyfield"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

The field name.

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
_java.io.IOException: No Resource Found: 'dido/oddjob/transform/DataSetExample.xml', classloader=java.net.URLClassLoader@8bda377_


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
