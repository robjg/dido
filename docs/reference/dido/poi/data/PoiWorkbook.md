[HOME](../../../README.md)
# dido-poi:workbook

A source or sink of data that is a Microsoft
Excel Spreadsheet.



### Property Summary

| Property | Description |
| -------- | ----------- |
| [input](#propertyinput) | An input type (i.e. | 
| [output](#propertyoutput) | An output type (i.e. | 
| [version](#propertyversion) | The version of Excel to create. | 
| [workbook](#propertyworkbook) |  | 


### Property Detail
#### input <a name="propertyinput"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>For reading yes but optional for writing.</td></tr>
</table>

An input type (i.e. file) that is an Excel
Workbook.

#### output <a name="propertyoutput"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>For writing yes, ignored for reading.</td></tr>
</table>

An output type (i.e. file) that is an Excel
Workbook.

#### version <a name="propertyversion"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Default to EXCEL2007.</td></tr>
</table>

The version of Excel to create. EXCEL97 or
EXCEL2007.

#### workbook <a name="propertyworkbook"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>




-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
