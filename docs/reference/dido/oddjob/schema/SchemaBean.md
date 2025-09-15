[HOME](../../../README.md)
# dido:schema

Define a Schema.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [defs](#propertydefs) | Schema definitions for nested schema references. | 
| [name](#propertyname) | The name of the schema. | 
| [of](#propertyof) | The fields. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Define a simple schema. |
| [Example 2](#example2) | Define a nested schema. |
| [Example 3](#example3) | Define a repeating schema. |


### Property Detail
#### defs <a name="propertydefs"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Schema definitions for nested schema references. These are generally defined with [dido:schema-defs](../../../dido/oddjob/schema/SchemaDefsBean.md)s.
These will be set automatically if this is a nested schema.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of the schema. This is so this schema may be
referenced elsewhere in the definition. If set then SchemaDefs must also be set.

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
                        <dido:field index="3" name="Fruit" type="java.lang.String"/>
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

Define a nested schema.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <defs>
                        <dido:schema-defs>
                            <schemas>
                                <dido:schema name="Fruit">
                                    <of>
                                        <dido:field index="3" name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field index="8" name="Price" type="double"/>
                                    </of>
                                </dido:schema>
                            </schemas>
                        </dido:schema-defs>
                    </defs>
                    <of>
                        <dido:field name="Name" type="java.lang.String"/>
                        <dido:field name="Fruit1" ref="Fruit"/>
                        <dido:field name="Fruit2" ref="Fruit"/>
                        <dido:field name="Drink">
                            <nested>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Name" type="java.lang.String"/>
                                        <dido:field name="Volume" type="double"/>
                                    </of>
                                </dido:schema>
                            </nested>
                        </dido:field>
                    </of>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Define a repeating schema.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <variables id="vars">
            <schema>
                <dido:schema xmlns:dido="oddjob:dido">
                    <defs>
                        <dido:schema-defs>
                            <schemas>
                                <dido:schema name="Fruit">
                                    <of>
                                        <dido:field index="3" name="Fruit" type="java.lang.String"/>
                                        <dido:field name="Qty" type="int"/>
                                        <dido:field index="8" name="Price" type="double"/>
                                    </of>
                                </dido:schema>
                            </schemas>
                        </dido:schema-defs>
                    </defs>
                    <of>
                        <dido:field name="Name" type="java.lang.String"/>
                        <dido:field name="Fruit" ref="Fruit" repeating="true"/>
                        <dido:field name="Drink" repeating="true">
                            <nested>
                                <dido:schema>
                                    <of>
                                        <dido:field name="Name" type="java.lang.String"/>
                                        <dido:field name="Volume" type="double"/>
                                    </of>
                                </dido:schema>
                            </nested>
                        </dido:field>
                    </of>
                </dido:schema>
            </schema>
        </variables>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
