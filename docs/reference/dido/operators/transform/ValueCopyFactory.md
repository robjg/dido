[HOME](../../../README.md)
# dido:copy

Copy a field from one position and/or field and/or type to another.



### Property Summary

| Property | Description |
| -------- | ----------- |
| [at](#propertyat) | Copy to the field of this index. | 
| [conversionProvider](#propertyconversionprovider) | A conversion provider to provide a conversion when a type is provided but a function isn't. | 
| [field](#propertyfield) | Copy from the field of this name. | 
| [function](#propertyfunction) | An optional Function that can be applied during the copy. | 
| [index](#propertyindex) | Copy from the field of this index. | 
| [to](#propertyto) | Copy to the field of this name. | 
| [type](#propertytype) | The to type of the new field. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Copy from one field to another. |
| [Example 2](#example2) | Copy primitives. |
| [Example 3](#example3) | Copy applying a function. |


### Property Detail
#### at <a name="propertyat"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The field will be copied either to the provided to field or to the same index.</td></tr>
</table>

Copy to the field of this index.

#### conversionProvider <a name="propertyconversionprovider"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A conversion provider to provide a conversion when a type is provided
but a function isn't. This will be injected by Oddjob if it can.

#### field <a name="propertyfield"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or index.</td></tr>
</table>

Copy from the field of this name.

#### function <a name="propertyfunction"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

An optional Function that can be applied during the copy.
The function must return a value assignable to the given type, or the existing field
type.

#### index <a name="propertyindex"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or field.</td></tr>
</table>

Copy from the field of this index.

#### to <a name="propertyto"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The field will be copied with the same name.</td></tr>
</table>

Copy to the field of this name.

#### type <a name="propertytype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The new field will of they type of the existing field.</td></tr>
</table>

The to type of the new field.


### Examples
#### Example 1 <a name="example1"></a>

Copy from one field to another.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <list>
                            <values>
                                <bean class="dido.oddjob.types.DidoDataType">
                                    <schema>
                                        <dido:schema xmlns:dido="oddjob:dido">
                                            <of>
                                                <dido:field name="One" type="int"/>
                                                <dido:field name="Two" type="int"/>
                                                <dido:field name="Three" type="int"/>
                                                <dido:field name="Four" type="int"/>
                                                <dido:field name="Five" type="int"/>
                                                <dido:field name="Six" type="int"/>
                                                <dido:field name="Seven" type="int"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                    <values>
                                        <list>
                                            <values>
                                                <value value="1"/>
                                                <value value="2"/>
                                                <value value="3"/>
                                                <value value="4"/>
                                                <value value="5"/>
                                                <value value="6"/>
                                                <value value="7"/>
                                            </values>
                                        </list>
                                    </values>
                                </bean>
                            </values>
                        </list>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:transform withCopy="true" xmlns:dido="oddjob:dido">
                            <of>
                                <dido:copy field="Six" to="SomeSix"/>
                                <dido:copy field="Three" to="SomeThree"/>
                                <dido:copy field="One" to="SomeOne"/>
                            </of>
                        </dido:transform>
                    </function>
                </bus:map>
                <bus:collect id="results"/>
            </of>
        </bus:bus>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Copy primitives.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <list>
                            <values>
                                <bean class="dido.operators.transform.ManyTypesRecord"/>
                            </values>
                        </list>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:transform xmlns:dido="oddjob:dido">
                            <of>
                                <dido:copy field="AnIntString" to="AByte">
                                    <type>
                                        <class name="byte"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="AnIntString" to="AShort">
                                    <type>
                                        <class name="short"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="AnIntString" to="AChar">
                                    <type>
                                        <class name="char"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="AnIntString" to="AnInt">
                                    <type>
                                        <class name="int"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="AnIntString" to="ALong">
                                    <type>
                                        <class name="long"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="ADoubleString" to="ADouble">
                                    <type>
                                        <class name="double"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="ADoubleString" to="AFloat">
                                    <type>
                                        <class name="float"/>
                                    </type>
                                </dido:copy>
                                <dido:copy field="ABooleanString" to="ABoolean">
                                    <type>
                                        <class name="boolean"/>
                                    </type>
                                </dido:copy>
                            </of>
                        </dido:transform>
                    </function>
                </bus:map>
                <bus:collect id="results"/>
            </of>
        </bus:bus>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

Copy applying a function.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <list>
                            <values>
                                <bean class="dido.oddjob.types.DidoDataType">
                                    <schema>
                                        <dido:schema xmlns:dido="oddjob:dido">
                                            <of>
                                                <dido:field name="AnInt" type="int"/>
                                                <dido:field name="BlankString" type="java.lang.String"/>
                                                <dido:field name="CsvString" type="java.lang.String"/>
                                                <dido:field name="NullString" type="java.lang.String"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                    <values>
                                        <list>
                                            <values>
                                                <value value="1"/>
                                                <value value=" "/>
                                                <value value="1,2"/>
                                            </values>
                                        </list>
                                    </values>
                                </bean>
                            </values>
                        </list>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:transform xmlns:dido="oddjob:dido">
                            <of>
                                <dido:copy field="AnInt">
                                    <function>
                                        <bean class="dido.operators.transform.ValueCopyFactoryTest$AddOne"/>
                                    </function>
                                </dido:copy>
                                <dido:copy field="BlankString">
                                    <function>
                                        <bean class="dido.operators.transform.ValueCopyFactoryTest$NullWhenBlank"/>
                                    </function>
                                </dido:copy>
                                <dido:copy field="CsvString">
                                    <type>
                                        <class name="[I"/>
                                    </type>
                                    <function>
                                        <bean class="dido.operators.transform.ValueCopyFactoryTest$SplitAndConvert"/>
                                    </function>
                                </dido:copy>
                                <dido:copy field="NullString">
                                    <function>
                                        <bean class="dido.operators.transform.ValueCopyFactoryTest$WhenNull"/>
                                    </function>
                                </dido:copy>
                            </of>
                        </dido:transform>
                    </function>
                </bus:map>
                <bus:collect id="results"/>
            </of>
        </bus:bus>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
