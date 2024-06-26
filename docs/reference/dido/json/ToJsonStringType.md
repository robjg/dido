[HOME](../../README.md)
# dido:to-json

Provides a Mapping Function that will convert a Dido Data
into a JSON String.

### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | From JSON Strings using a Mapping function and back again. |


### Examples
#### Example 1 <a name="example1"></a>

From JSON Strings using a Mapping function and back again.
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <bus:bus xmlns:bus="oddjob:beanbus">
            <of>
                <bus:driver>
                    <values>
                        <buffer>
                            <![CDATA[{ "Fruit"="Apple", "Qty"=5, "Price"=27.2 }
{ "Fruit"="Orange", "Qty"=10, "Price"=31.6 }
{ "Fruit"="Pear", "Qty"=7, "Price"=22.1 }
]]>
                        </buffer>
                    </values>
                </bus:driver>
                <bus:map>
                    <function>
                        <dido:from-json xmlns:dido="oddjob:dido"/>
                    </function>
                </bus:map>
                <bus:map>
                    <function>
                        <dido:to-json xmlns:dido="oddjob:dido"/>
                    </function>
                    <to>
                        <identify id="results">
                            <value>
                                <buffer/>
                            </value>
                        </identify>
                    </to>
                </bus:map>
            </of>
        </bus:bus>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
