[HOME](../../../README.md)
# dido:field

Define the field of a Schema. See [dido:schema](../../../dido/oddjob/schema/SchemaBean.md) for examples.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [index](#propertyindex) | The index of the field. | 
| [name](#propertyname) | The name of the field. | 
| [nested](#propertynested) | A nested schema. | 
| [ref](#propertyref) | A reference to a nested schema defined elsewhere. | 
| [repeating](#propertyrepeating) | Is the nested schema repeating. | 
| [type](#propertytype) | The type of the field. | 


### Property Detail
#### index <a name="propertyindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The next available field will be used.</td></tr>
</table>

The index of the field.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the field.

#### nested <a name="propertynested"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A nested schema.

#### ref <a name="propertyref"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A reference to a nested schema defined elsewhere.

#### repeating <a name="propertyrepeating"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Is the nested schema repeating.

#### type <a name="propertytype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes, unless nested.</td></tr>
</table>

The type of the field.


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
