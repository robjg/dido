[HOME](../../../README.md)
# dido:schema

Define a Schema.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [list](#propertylist) | Nested Schemas. | 
| [name](#propertyname) | The name of the schema. | 
| [named](#propertynamed) | Reference a schema by name. | 
| [of](#propertyof) | The fields. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Define a simple schema. |
| [Example 2](#example2) | Define a simple schema. |
| [Example 3](#example3) | Define a nested schema. |
| [Example 4](#example4) | Define a repeating schema. |


### Property Detail
#### list <a name="propertylist"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Nested Schemas.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the schema. This is so this schema  may be
referenced elsewhere in the definition.

#### named <a name="propertynamed"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Reference a schema by name. Used when a schema is a nested schema and pulls in
a schema definition named elsewhere in the overall schema definition.

#### of <a name="propertyof"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The fields. These are generally defined with [dido:field](../../../dido/oddjob/schema/SchemaFieldBean.md)s.


### Examples
#### Example 1 <a name="example1"></a>

Define a simple schema.
```xml
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <of>
                        <dido:field index="3" name="Fruit"/>
                        <dido:field type="int" name="Qty"/>
                        <dido:field type="double" index="8" name="Price"/>
                    </of>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Define a simple schema.
```xml
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <of>
                        <dido:field index="3" name="Fruit"/>
                        <dido:field type="int" name="Qty"/>
                        <dido:field type="double" index="8" name="Price"/>
                    </of>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Define a nested schema.
```xml
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <list>
                        <dido:schema name="Fruit">
                            <of>
                                <dido:field index="3" name="Fruit"/>
                                <dido:field type="int" name="Qty"/>
                                <dido:field type="double" index="8" name="Price"/>
                            </of>
                        </dido:schema>
                        <dido:schema>
                            <of>
                                <dido:field name="Name"/>
                                <dido:field name="Fruit1">
                                    <nested>
                                        <dido:schema named="Fruit"/>
                                    </nested>
                                </dido:field>
                                <dido:field name="Fruit2">
                                    <nested>
                                        <dido:schema named="Fruit"/>
                                    </nested>
                                </dido:field>
                                <dido:field name="Drink">
                                    <nested>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="Name"/>
                                                <dido:field name="Volume" type="double"/>
                                            </of>
                                        </dido:schema>
                                    </nested>
                                </dido:field>
                            </of>
                        </dido:schema>
                    </list>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```


#### Example 4 <a name="example4"></a>

Define a repeating schema.
```xml
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <list>
                        <dido:schema name="Fruit">
                            <of>
                                <dido:field index="3" name="Fruit"/>
                                <dido:field type="int" name="Qty"/>
                                <dido:field type="double" index="8" name="Price"/>
                            </of>
                        </dido:schema>
                        <dido:schema>
                            <of>
                                <dido:field name="Name"/>
                                <dido:field name="Fruit" repeating="true">
                                    <nested>
                                        <dido:schema named="Fruit"/>
                                    </nested>
                                </dido:field>
                                <dido:field name="Drink" repeating="true">
                                    <nested>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="Name"/>
                                                <dido:field name="Volume" type="double"/>
                                            </of>
                                        </dido:schema>
                                    </nested>
                                </dido:field>
                            </of>
                        </dido:schema>
                    </list>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
