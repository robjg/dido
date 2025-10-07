[HOME](../../../README.md)
# dido:schema-from

Create a Schema from another schema by including, merging, concatenating
or excluding fields. The changes are applied in that order.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [concat](#propertyconcat) | The schemas to concatenate. | 
| [exclude](#propertyexclude) | The fields to exclude. | 
| [from](#propertyfrom) | The schema to start from. | 
| [include](#propertyinclude) | The fields to include from the schema. | 
| [merge](#propertymerge) | The schemas to merge. | 
| [name](#propertyname) | The name of the schema. | 
| [reIndex](#propertyreindex) | Reindex the fields to remove gaps. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Exclude fields. |
| [Example 2](#example2) | Merge another schema. |
| [Example 3](#example3) | Concatenate another schema. |
| [Example 4](#example4) | Define a nested schema that is a combination of two schemas. |


### Property Detail
#### concat <a name="propertyconcat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schemas to concatenate.

#### exclude <a name="propertyexclude"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The fields to exclude.

#### from <a name="propertyfrom"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The schema to start from.

#### include <a name="propertyinclude"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The fields to include from the schema. The same
as excluding all the other fields in the schema.

#### merge <a name="propertymerge"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The schemas to merge.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the schema. This is so this schema may be
referenced elsewhere in the definition. If set then SchemaDefs must also be set, either directly
or because this is nested within another Schema.

#### reIndex <a name="propertyreindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Reindex the fields to remove gaps.


### Examples
#### Example 1 <a name="example1"></a>

Exclude fields.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <schema>
                        <dido:schema-from exclude="Qty,Colour" reIndex="true" xmlns:dido="oddjob:dido">
                            <from>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field name="Price" type="double"/>
                                        <dido:field name="Colour" type="java.lang.String"/>
                                    </of>
                                </dido:schema>
                            </from>
                        </dido:schema-from>
                    </schema>
                    <expected>
                        <dido:schema xmlns:dido="oddjob:dido">
                            <of>
                                <dido:field name="Fruit" type="java.lang.String"/>
                                <dido:field name="Price" type="double"/>
                            </of>
                        </dido:schema>
                    </expected>
                </variables>
                <script resultForState="true">
                    <beans>
                        <value key="actual" value="${vars.schema}"/>
                        <value key="expected" value="${vars.expected}"/>
                    </beans><![CDATA[expected.equals(actual) ? 0 : 1
]]></script>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Merge another schema.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <schema>
                        <dido:schema-from xmlns:dido="oddjob:dido">
                            <from>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field name="Price" type="double"/>
                                    </of>
                                </dido:schema>
                            </from>
                            <merge>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Qty" type="double"/>
                                        <dido:field name="Colour" type="java.lang.String"/>
                                    </of>
                                </dido:schema>
                            </merge>
                        </dido:schema-from>
                    </schema>
                    <expected>
                        <dido:schema xmlns:dido="oddjob:dido">
                            <of>
                                <dido:field name="Fruit" type="java.lang.String"/>
                                <dido:field name="Qty" type="double"/>
                                <dido:field name="Price" type="double"/>
                                <dido:field name="Colour" type="java.lang.String"/>
                            </of>
                        </dido:schema>
                    </expected>
                </variables>
                <script resultForState="true">
                    <beans>
                        <value key="actual" value="${vars.schema}"/>
                        <value key="expected" value="${vars.expected}"/>
                    </beans><![CDATA[expected.equals(actual) ? 0 : 1
]]></script>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Concatenate another schema.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <schema>
                        <dido:schema-from xmlns:dido="oddjob:dido">
                            <from>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field name="Price" type="double"/>
                                    </of>
                                </dido:schema>
                            </from>
                            <concat>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Qty" type="double"/>
                                        <dido:field name="Colour" type="java.lang.String"/>
                                    </of>
                                </dido:schema>
                            </concat>
                        </dido:schema-from>
                    </schema>
                    <expected>
                        <dido:schema xmlns:dido="oddjob:dido">
                            <of>
                                <dido:field name="Fruit" type="java.lang.String"/>
                                <dido:field name="Qty" type="int"/>
                                <dido:field name="Price" type="double"/>
                                <dido:field name="Qty_" type="double"/>
                                <dido:field name="Colour" type="java.lang.String"/>
                            </of>
                        </dido:schema>
                    </expected>
                </variables>
                <script resultForState="true">
                    <beans>
                        <value key="actual" value="${vars.schema}"/>
                        <value key="expected" value="${vars.expected}"/>
                    </beans><![CDATA[expected.equals(actual) ? 0 : 1
]]></script>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 4 <a name="example4"></a>

Define a nested schema that is a combination
of two schemas. Also shows how schemas can be referenced from
variables in Oddjob.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <schema>
                        <dido:schema-from xmlns:dido="oddjob:dido">
                            <from>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field name="Price" type="double"/>
                                    </of>
                                </dido:schema>
                            </from>
                            <concat>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Qty" type="double"/>
                                        <dido:field name="Colour" type="java.lang.String"/>
                                    </of>
                                </dido:schema>
                            </concat>
                        </dido:schema-from>
                    </schema>
                    <expected>
                        <dido:schema xmlns:dido="oddjob:dido">
                            <of>
                                <dido:field name="Fruit" type="java.lang.String"/>
                                <dido:field name="Qty" type="int"/>
                                <dido:field name="Price" type="double"/>
                                <dido:field name="Qty_" type="double"/>
                                <dido:field name="Colour" type="java.lang.String"/>
                            </of>
                        </dido:schema>
                    </expected>
                </variables>
                <script resultForState="true">
                    <beans>
                        <value key="actual" value="${vars.schema}"/>
                        <value key="expected" value="${vars.expected}"/>
                    </beans><![CDATA[expected.equals(actual) ? 0 : 1
]]></script>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
