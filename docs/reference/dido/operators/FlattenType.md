[HOME](../../README.md)
# dido:flatten

Provides a mapping function that either flattens fields that are
collections or arrays or a nested repeating schema. The result is a List of `DidoData`

### Property Summary

| Property | Description |
| -------- | ----------- |
| [columns](#propertycolumns) | Flatten the fields as columns. | 
| [fields](#propertyfields) | The comma separated list of fields to flatten. | 
| [schema](#propertyschema) | The nested schema to flatten, if known. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Flatten a nested schema. |
| [Example 2](#example2) | Flatten columns that are arrays. |


### Property Detail
#### columns <a name="propertycolumns"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, defaults to false.</td></tr>
</table>

Flatten the fields as columns.

#### fields <a name="propertyfields"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>yes.</td></tr>
</table>

The comma separated list of fields to flatten. Only one nested
Schema field is supported (if columns is false)

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, it will be discovered.</td></tr>
</table>

The nested schema to flatten, if known.


### Examples
#### Example 1 <a name="example1"></a>

Flatten a nested schema.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in name="Data" xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json format="ARRAY" partialSchema="true">
                                    <schema>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="OrderLines" repeating="true">
                                                    <nested>
                                                        <dido:schema>
                                                            <of>
                                                                <dido:field name="Qty" type="int"/>
                                                            </of>
                                                        </dido:schema>
                                                    </nested>
                                                </dido:field>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                </dido:json>
                            </how>
                            <from>
                                <buffer>
                                    <![CDATA[[
  { "OrderId": "A123", 
    "OrderLines": [ 
      {"Fruit": "Apple", "Qty": 4}, 
      {"Fruit": "Pear", "Qty": 5}
    ]
  }
]]]>
                                </buffer>
                            </from>
                        </dido:data-in>
                        <bus:map>
                            <function>
                                <dido:flatten fields="OrderLines" xmlns:dido="oddjob:dido"/>
                            </function>
                        </bus:map>
                        <bean class="org.oddjob.beanbus.destinations.UnPack"/>
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

Flatten columns that are arrays.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in name="Data" xmlns:dido="oddjob:dido">
                            <how>
                                <dido:json format="ARRAY" partialSchema="true">
                                    <schema>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="Numbers" type="[I"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                </dido:json>
                            </how>
                            <from>
                                <buffer>
                                    <![CDATA[[
  { "Name": "Foo", 
    "Numbers": [ 1, 2, 3 ],
    "Letters": [ "X", "Y" ]
  }
]]]>
                                </buffer>
                            </from>
                        </dido:data-in>
                        <bus:map>
                            <function>
                                <dido:flatten columns="true" fields="Numbers,Letters" xmlns:dido="oddjob:dido"/>
                            </function>
                        </bus:map>
                        <bean class="org.oddjob.beanbus.destinations.UnPack"/>
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
