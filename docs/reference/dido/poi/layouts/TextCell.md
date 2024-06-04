[HOME](../../../README.md)
# dido-poi:text

Read to and from a series of cells. Currently only
the ability to read columns of data by nesting within a [dido-poi:rows](../../../dido/poi/layouts/DataRows.md)
is supported but the ability to read rows will be added at some point.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [cellType](#propertycellType) | The Excel type of this column. | 
| [defaultStyle](#propertydefaultStyle) | The default style for the cell. | 
| [index](#propertyindex) | The 1 based column index of this layout. | 
| [name](#propertyname) | The name of this layout. | 
| [reference](#propertyreference) | The Excel reference of the last row of this column that has been written. | 
| [style](#propertystyle) | The name of the style to use. | 
| [type](#propertytype) |  | 
| [value](#propertyvalue) |  | 


### Property Detail
#### cellType <a name="propertycellType"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The Excel type of this column.

#### defaultStyle <a name="propertydefaultStyle"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The default style for the cell. This is a
visible property of the layout so that users can see which style
name to use if they wish to override the style at the
[dido-poi:workbook](../../../dido/poi/data/PoiWorkbook.md) level.

#### index <a name="propertyindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The 1 based column index of this layout.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of this layout. The name is the main
identification of a layout node. It is commonly used by bindings to
associate with the property name of a Java Object.

#### reference <a name="propertyreference"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Read only.</td></tr>
</table>

The Excel reference of the last row of this
column that has been written.

#### style <a name="propertystyle"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the style to use. The style will have
been defined with the [dido-poi:workbook](../../../dido/poi/data/PoiWorkbook.md) definition.

#### type <a name="propertytype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>



#### value <a name="propertyvalue"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>




-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
