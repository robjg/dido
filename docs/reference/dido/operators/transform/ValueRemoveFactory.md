[HOME](../../../README.md)
# dido:remove

Remove the value for a field or index. Participates in an [dido:transform](../../../dido/operators/transform/TransformationFactory.md).

### Property Summary

| Property | Description |
| -------- | ----------- |
| [at](#propertyat) | The index to set the value at. | 
| [field](#propertyfield) | The field name. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Removes a value by Field Name. |


### Property Detail
#### at <a name="propertyat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

The index to set the value at.

#### field <a name="propertyfield"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No</td></tr>
</table>

The field name.


### Examples
#### Example 1 <a name="example1"></a>

Removes a value by Field Name. The actual example is
a single element of XML half-way down the configuration. First this example
sets up the schema and expected schemas. Then it creates 3 items
of data. It pipes these through a transformation that removes the Qty
field from each item. It collects this data and then finally uses
JavaScript again to verify the result.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <schema>
                        <dido:schema xmlns:dido="oddjob:dido">
                            <of>
                                <dido:field name="Fruit" type="java.lang.String"/>
                                <dido:field name="Qty" type="int"/>
                                <dido:field name="Price" type="double"/>
                            </of>
                        </dido:schema>
                    </schema>
                    <expectedSchema>
                        <dido:schema-from exclude="Qty" reIndex="true" xmlns:dido="oddjob:dido">
                            <from>
                                <value value="${vars.schema}"/>
                            </from>
                        </dido:schema-from>
                    </expectedSchema>
                </variables>
                <script id="defineData" name="Define Test Data">
                    <beans>
                        <value key="schema" value="${vars.schema}"/>
                    </beans><![CDATA[
                    Java.type("dido.data.DidoData").withSchema(schema).many()
                    .of("Apple", 5, 27.2)
                    .of("Orange", 10, 31.6)
                    .of("Pear", 7, 22.1)
                    .toList()
                ]]></script>
                <bus:bus name="Pipe Test Data to Result Data with Transformation" xmlns:bus="oddjob:beanbus">
                    <of>
                        <bus:driver>
                            <values>
                                <value value="${defineData.result}"/>
                            </values>
                        </bus:driver>
                        <bus:map name="Perform the Transform">
                            <function>
                                <dido:transform reIndex="true" withExisting="true" xmlns:dido="oddjob:dido">
                                    <of>
                                        <dido:remove field="Qty"/>
                                    </of>
                                </dido:transform>
                            </function>
                        </bus:map>
                        <bus:collect id="capture"/>
                    </of>
                </bus:bus>
                <script name="Verify Result" resultForState="true">
                    <beans>
                        <value key="expectedSchema" value="${vars.expectedSchema}"/>
                        <value key="actual" value="${capture.beans.list}"/>
                    </beans><![CDATA[var expected = Java.type("dido.data.DidoData").withSchema(expectedSchema).many()
                    .of("Apple", 27.2)
                    .of("Orange", 31.6)
                    .of("Pear", 22.1)
                    .toList()
var actualSchema = actual.get(0).getSchema()
if (expectedSchema.equals(actualSchema)) {
  if (expected.equals(actual)) {
     0
  }
  else {
    print("Expected data did not match actual")
    print(expected)
    print(actual)
    1
  }
}
else {
  print("Expected Schema did not match actual")
  print(expectedSchema)
  print(actualSchema)
  1
}
]]></script>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
