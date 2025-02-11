[HOME](../../../README.md)
# dido:stream-join

A service that Joins two sources of `dido.data.DidoData` into a single
destination.

For examples see [dido:left-join](../../../dido/operators/join/LeftStreamJoinType.md).

### Property Summary

| Property | Description |
| -------- | ----------- |
| [count](#propertycount) | The number of items sent to the destination. | 
| [join](#propertyjoin) | The join operation. | 
| [name](#propertyname) | The name of the component. | 
| [primary](#propertyprimary) | The primary source of data. | 
| [secondary](#propertysecondary) | The secondary source of data. | 
| [to](#propertyto) | The destination. | 


### Property Detail
#### count <a name="propertycount"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The number of items sent to the destination.

#### join <a name="propertyjoin"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The join operation.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the component.

#### primary <a name="propertyprimary"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The primary source of data.

#### secondary <a name="propertysecondary"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read Only.</td></tr>
</table>

The secondary source of data.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, set automatically by BeanBus.</td></tr>
</table>

The destination.


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
