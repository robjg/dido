The Dido Oddball
================

An Oddball is an Oddjob module that plugs into Oddjob in a way that keeps its dependencies separate from
other Oddjob Oddballs.

If you use the Downloaded Oddjob Application then Dido can be resolved
as an 'Oddball'. Here is the configuration to enable the example to be run in this
way:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<oddjob id="oddjob">
    <job>
        <sequential>
            <jobs>
                <resolve:resolve id="resolveDido" xmlns:resolve="oddjob:resolve">
                    <dependencies>
                        <resolve:dependency artifactId="dido-all" groupId="uk.co.rgordon" type="pom" version="2.0.0-SNAPSHOT"/>
                    </dependencies>
                </resolve:resolve>
                <copy name="Copy Sample File" to="Fruit.csv">
                    <from>
                        <file file="${oddjob.dir}/../data/FruitNoHeader.csv"/>
                    </from>
                </copy>
                <oddjob file="${oddjob.dir}/CsvToJson.xml">
                    <descriptorFactory>
                        <oddballs>
                            <oddballs>
                                <oddball>
                                    <paths>
                                        <value value="${resolveDido.resolvedPathsArray}"/>
                                    </paths>
                                </oddball>
                            </oddballs>
                        </oddballs>
                    </descriptorFactory>
                </oddjob>
                <delete>
                    <files>
                        <file file="Fruit.csv"/>
                    </files>
                </delete>
            </jobs>
        </sequential>
    </job>
</oddjob>
```

Obviously this complicates the configuration somewhat. It would be
quite simple to create an Oddjob that copied the resolved files into Oddjob's Oddball
directory so that Dido was permanently available within Oddjob. 
