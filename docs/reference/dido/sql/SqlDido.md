[HOME](../../README.md)
# dido:sql

Export and Import with SQL.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [batchSize](#propertybatchSize) | Batch size. | 
| [classLoader](#propertyclassLoader) | The classloader used to derive the schema from a query. | 
| [schema](#propertyschema) | An override schema that supplies a desired type to the underlying [java.sql.ResultSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html) method. | 
| [sql](#propertysql) | The SQL that will extract or insert the data. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Insert and Extract data. |


### Property Detail
#### batchSize <a name="propertybatchSize"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Batch size. This is passed directly to JDBC.

#### classLoader <a name="propertyclassLoader"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The classloader used to derive the schema from a query. Only required if the
table include custom data types.

#### schema <a name="propertyschema"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

An override schema that supplies a desired type to the underlying
[java.sql.ResultSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html) method. Weather this is honoured is
dependent on the JDBC implementation.

#### sql <a name="propertysql"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The SQL that will extract or insert the data.


### Examples
#### Example 1 <a name="example1"></a>

Insert and Extract data.


```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob>
    <job>
        <sequential>
            <jobs>
                <variables id="vars">
                    <connection>
                        <connection driver="org.hsqldb.jdbcDriver" password="" url="jdbc:hsqldb:mem:test" username="sa"/>
                    </connection>
                </variables>
                <sql>
                    <connection>
                        <value value="${vars.connection}"/>
                    </connection>
                    <input>
                        <buffer>
                            <![CDATA[
              DROP TABLE fruit IF EXISTS

              CREATE TABLE fruit(
              TYPE VARCHAR(20),
              QUANTITY INTEGER)]]>
                        </buffer>
                    </input>
                </sql>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in xmlns:dido="oddjob:dido">
                            <how>
                                <dido:csv>
                                    <schema>
                                        <dido:schema>
                                            <of>
                                                <dido:field name="TYPE" type="java.lang.String"/>
                                                <dido:field name="QUANTITY" type="int"/>
                                            </of>
                                        </dido:schema>
                                    </schema>
                                </dido:csv>
                            </how>
                            <from>
                                <buffer>
                                    <![CDATA[Apple,20
Orange,30
Pear,40
Grape,55]]>
                                </buffer>
                            </from>
                        </dido:data-in>
                        <dido:data-out xmlns:dido="oddjob:dido">
                            <how>
                                <dido:sql sql="insert into fruit (type, quantity) values (?,?)"/>
                            </how>
                            <to>
                                <value value="${vars.connection}"/>
                            </to>
                        </dido:data-out>
                    </of>
                </bus:bus>
                <bus:bus xmlns:bus="oddjob:beanbus">
                    <of>
                        <dido:data-in xmlns:dido="oddjob:dido">
                            <how>
                                <dido:sql sql="select * from fruit"/>
                            </how>
                            <from>
                                <value value="${vars.connection}"/>
                            </from>
                        </dido:data-in>
                        <bus:map>
                            <function>
                                <dido:transform withCopy="true" xmlns:dido="oddjob:dido"/>
                            </function>
                        </bus:map>
                        <bus:collect id="results"/>
                    </of>
                </bus:bus>
            </jobs>
        </sequential>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
