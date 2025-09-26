[HOME](../../../README.md)
# dido:left-join

A join operation that performs a full left join.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [foreignFields](#propertyforeignfields) | The names of the fields that form the foreign key in the primary data. | 
| [foreignIndices](#propertyforeignindices) | The indices of the fields that form the foreign key in the primary data. | 
| [primaryFields](#propertyprimaryfields) | The names of the fields that are the key of the primary data. | 
| [primaryIndices](#propertyprimaryindices) | The indices of the fields that are the key of the primary data. | 
| [secondaryFields](#propertysecondaryfields) | The names of the fields that form the key in the secondary data. | 
| [secondaryIndices](#propertysecondaryindices) | The indices of the fields that form the key in the secondary data. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Join based on indices. |
| [Example 2](#example2) | Join based on field names. |
| [Example 3](#example3) | A more complicated Join. |


### Property Detail
#### foreignFields <a name="propertyforeignfields"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Foreign Indices are required.</td></tr>
</table>

The names of the fields that form the foreign key in the primary data. The data in
these fields must match that in the Secondary key fields by value and type for a match.

#### foreignIndices <a name="propertyforeignindices"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Foreign Fields are required.</td></tr>
</table>

The indices of the fields that form the foreign key in the primary data. The data in
these fields must match that in the Secondary key fields by value and type for a match.

#### primaryFields <a name="propertyprimaryfields"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Primary Indices are required.</td></tr>
</table>

The names of the fields that are the key of the primary data.

#### primaryIndices <a name="propertyprimaryindices"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Primary Names are required.</td></tr>
</table>

The indices of the fields that are the key of the primary data.

#### secondaryFields <a name="propertysecondaryfields"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Secondary Indices are required.</td></tr>
</table>

The names of the fields that form the key in the secondary data. The data in
these fields must match that in the Foreign fields by value and type for a match.

#### secondaryIndices <a name="propertysecondaryindices"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Either this or Secondary Fields are required.</td></tr>
</table>

The indices of the fields that form the key in the secondary data. The data in
these fields must match that in the Foreign fields by value and type for a match.


### Examples
#### Example 1 <a name="example1"></a>

Join based on indices.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <parallel>
                            <jobs>
                                <dido:data-in id="fruit" name="Fruit Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Type" type="java.lang.String"/>
                                                        <dido:field name="Quantity" type="int"/>
                                                        <dido:field name="FarmId" type="int"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[Apples,12,2
Pears,7,1
Carrots,15,2
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.primary}"/>
                                    </to>
                                </dido:data-in>
                                <dido:data-in id="farmers" name="Farmer Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Id" type="int"/>
                                                        <dido:field name="Farmer" type="java.lang.String"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[1,Brown
2,Giles
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.secondary}"/>
                                    </to>
                                </dido:data-in>
                            </jobs>
                        </parallel>
                        <dido:stream-join id="join" xmlns:dido="oddjob:dido">
                            <join>
                                <dido:left-join foreignIndices="3" primaryIndices="1" secondaryIndices="1"/>
                            </join>
                        </dido:stream-join>
                        <bus:collect id="results"/>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:csv/>
                            </how>
                            <to>
                                <stdout/>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 2 <a name="example2"></a>

Join based on field names.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <parallel>
                            <jobs>
                                <dido:data-in id="fruit" name="Fruit Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Type" type="java.lang.String"/>
                                                        <dido:field name="Quantity" type="int"/>
                                                        <dido:field name="FarmId" type="int"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[Apples,12,2
Pears,7,1
Carrots,15,2
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.primary}"/>
                                    </to>
                                </dido:data-in>
                                <dido:data-in id="farmers" name="Farmer Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Id" type="int"/>
                                                        <dido:field name="Farmer" type="java.lang.String"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[1,Brown
2,Giles
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.secondary}"/>
                                    </to>
                                </dido:data-in>
                            </jobs>
                        </parallel>
                        <dido:stream-join id="join" xmlns:dido="oddjob:dido">
                            <join>
                                <dido:left-join foreignFields="FarmId" primaryFields="Type" secondaryFields="Id"/>
                            </join>
                        </dido:stream-join>
                        <bus:collect id="results"/>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:csv/>
                            </how>
                            <to>
                                <stdout/>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```


#### Example 3 <a name="example3"></a>

A more complicated Join.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <parallel>
                            <jobs>
                                <dido:data-in id="fruit" name="Fruit Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Type" type="java.lang.String"/>
                                                        <dido:field name="Variety" type="java.lang.String"/>
                                                        <dido:field name="Quantity" type="int"/>
                                                        <dido:field name="FarmId" type="int"/>
                                                        <dido:field name="Country" type="java.lang.String"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[Apples,Cox,12,2,UK
Pears,Conference,7,1,FR
Carrots,,15,2,UK
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.primary}"/>
                                    </to>
                                </dido:data-in>
                                <dido:data-in id="farmers" name="Farmer Data" xmlns:dido="oddjob:dido">
                                    <how>
                                        <dido:csv>
                                            <schema>
                                                <dido:schema>
                                                    <of>
                                                        <dido:field name="Id" type="int"/>
                                                        <dido:field name="Country" type="java.lang.String"/>
                                                        <dido:field name="Farmer" type="java.lang.String"/>
                                                    </of>
                                                </dido:schema>
                                            </schema>
                                        </dido:csv>
                                    </how>
                                    <from>
                                        <buffer>
                                            <![CDATA[1,FR,Brun
2,UK,Giles
]]>
                                        </buffer>
                                    </from>
                                    <to>
                                        <value value="${join.secondary}"/>
                                    </to>
                                </dido:data-in>
                            </jobs>
                        </parallel>
                        <dido:stream-join id="join" xmlns:dido="oddjob:dido">
                            <join>
                                <dido:left-join foreignFields="FarmId, Country" primaryFields="Type, Variety" secondaryFields="Id, Country"/>
                            </join>
                        </dido:stream-join>
                        <bus:collect id="results"/>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:csv/>
                            </how>
                            <to>
                                <stdout/>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
