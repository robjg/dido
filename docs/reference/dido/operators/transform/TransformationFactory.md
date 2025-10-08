[HOME](../../../README.md)
# dido:transform

Create a transformation of data by applying field transformations.


By default, only
fields resulting from a transformation will appear in the resultant data. If you include all other existing fields
in a transformation then set the `withExisting` property to `true`. If you wish to include most, but not
all fields, use the `withExisting` property in conjunction with the [dido:remove](../../../dido/operators/transform/ValueRemoveFactory.md) transformation.


The resultant data is by default, a view of the original data. Any functions on the data are applied every time
that field is read. This is the most performant approach for simple in-out pipelines. If the data is going to
be read many times, then set the `copy` property to  `true` so the operations are only performed once.


For examples see [dido:set](../../../dido/operators/transform/ValueSetFactory.md), [dido:copy](../../../dido/operators/transform/ValueCopyFactory.md) and [dido:remove](../../../dido/operators/transform/ValueRemoveFactory.md)

### Property Summary

| Property | Description |
| -------- | ----------- |
| [copy](#propertycopy) | Include existing fields before applying transformations. | 
| [dataFactoryProvider](#propertydatafactoryprovider) | A factory for creating the new Data. | 
| [of](#propertyof) | The field level transformations to apply. | 
| [reIndex](#propertyreindex) | If fields have been removed there will be wholes in the schema where indices are missing. | 
| [withExisting](#propertywithexisting) | Include existing fields before applying transformations. | 


### Property Detail
#### copy <a name="propertycopy"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Include existing fields before applying transformations.

#### dataFactoryProvider <a name="propertydatafactoryprovider"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Defaults to something reasonable.</td></tr>
</table>

A factory for creating the new Data. This is only applicable
if `copy` is true, as no new data is created when {code copy} is false.

#### of <a name="propertyof"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No. Will just copy or create empty data.</td></tr>
</table>

The field level transformations to apply.

#### reIndex <a name="propertyreindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

If fields have been removed there will be wholes in
the schema where indices are missing. Setting this property to true
ensures the resultant schema has indices 1, 2, 3, etc.

#### withExisting <a name="propertywithexisting"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Include existing fields before applying transformations.


-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
