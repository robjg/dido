# Background to Dido

Why oh why another ETl / EAI / Data Streaming framework?

## Beginnings

### 2006

Dido started some time around 2006. I was using Oddjob a lot for automated testing
and was also working with an early EAI tool called [OpenAdaptor](https://github.com/openadaptor/openadaptor)
OpenAdaptor was configured with Java Properties and had no monitoring.
So I thought wouldn't it be great to have something like OpenAdaptor but was
configurable and monitorable within Oddjob. 

OpenAdaptor and many other frameworks like it use schemaless internal data structures
akin to a Map as their data model. This has limitations when you need
to write a custom processor for a pipeline because you are always at the mercy
of runtime type cast exceptions. There was a lot of mapping to Java Beans happening
at the time, with the likes of Hibernate becoming ubiquitous. It seemed like the
way to go was to convert everything to a Java Bean and this is what the first
iteration of Dido did. It worked and it ran in Oddjob. It moved data from 
and to Csv, Sql and Excel. It tried to be too clever in attempting to anticipate any 
incarnation of data shape (what if data were spread over several lines) and was thus slow, 
difficult to understand and debug. 

Over the years I used it mainly for testing in combination with [Beancmpr](https://github.com/robjg/beancmpr). 
It proved particularly useful during a database migration but I was always conscious that
I'd just got it wrong.

### 2020

In 2020, I started working for an organisation that used a lot of proprietary 
high performance message formats, and needed to compare them. Should I convert
all the messages to JavaBeans? 

I realised that all I need was an Interface that could sit over the top of these
proprietary message formats and make them look the same. The data also contained
a lot of primitives, and although performance wasn't a number one priority, I
didn't want to preclude it by anything that used [Autoboxing](https://docs.oracle.com/javase/8/docs/technotes/guides/language/autoboxing.html)
which any reflective conversion to an Object would involve.

Googling for 'Java Generic Data' and similar I found Avro's [GenericRecord](https://avro.apache.org/docs/1.7.6/api/java/org/apache/avro/generic/GenericRecord.html) 
but not a lot else. This was sort of what I was looking for but the Schema that needs to
be defined along with the record is a little intimidating. Basically what I needed was JDBC's [ResultSet](https://docs.oracle.com/en/java/javase/17/docs//api/java.sql/java/sql/ResultSet.html) except
without the Database stuff, and a bit simpler. I decided I had to write my own
[GenericData](https://javadoc.io/doc/uk.co.rgordon/dido-data/latest/dido/data/GenericData.html).
The Field Type is Generic. This is because the project I was working on at the time made heavy
use of EnumMaps as they are more performant than HashMaps, so I decided I needed
to support data with not just String field name access. This complicates the code
but allows for more performant access.

And over the following years Dido grew from this starting point. The old formatters
were re-written but only in the simplest way possible. Wrap an existing library, not 
start from the ground up. Don't convert - just wrap became my new mantra.

### 2024

At the time of writing in the Spring of 2024 I'm looking at a chunk of work to 
simplify some more testing I need to do, and coming back to Dido always make
me think - Is this worth it? What do I need? and isn't there something already out 
there. So I began one of my 'every few years' trawl of the EAI landscape.

These are my use cases:
- Compare data from different sources (i.e. database and csv).
- Read and write from different sources to flat files to create test sets.
- Rename and convert fields in flight. 
- Join and Flatten one to many data.
- Record and playback data for testing.
- Orchestration via a task Automation framework.

I'm going to ignore the comparison use case as that belongs in [Beancmpr](https://github.com/robjg/beancmpr)
and most frameworks can convert to Beans, and Beancmpr as the name implies, can
compare Beans.

## The Trawl

### [Apache Camel](https://camel.apache.org/)

Camel provides loads of endpoints and formats for consuming and producing data. It's one of
the best known and popular data integration frameworks. 

Integration between Dido and Camel will definitely be looked at using a 
Custom [DataFormat](https://www.javadoc.io/doc/org.apache.camel/camel-api/latest/org/apache/camel/spi/DataFormat.html).

### [Spring Integration](https://spring.io/projects/spring-integration)
Like Camel, loads of endpoints, and a Generic payload that could be Dido Data.

### [Mule ESB](https://github.com/mulesoft/mule.git)

This is a framework that I've come back to many times over the years. At the time
of writing it appears to be increasingly less Open Source. The core is on Github but
wouldn't check out on Windows due to a long filename path. I'm not going to come
back to this again.

## Dataframes

There are several projects that create the equivalent of Python Pandas Dataframes. These projects 
allow reading and writing from common data formats in an internal structure and
 then apply transformations so there is quite a lot of overlap with Dido.

#### [dflib](https://dflib.org/)
Impressive library. Currently under active development. Supports data from and to
several sources including Beans, CSV, JSON, SQL. A nice API and well documented

#### [tablesaw](https://github.com/jtablesaw/tablesaw)
Also impressive, under active development and supports lots of sources. Emphasis appears
to be on Visualisation but has pretty much the same functionality as dflib.

#### [Java DataFrame](https://github.com/nRo/DataFrame) 
Hasn't been updated in several years. Only supports data from CSV sources. Feels a
bit unfinished.

## Schemas

I'm very conscious of having created Yet Another Schema Definition. In addition to
[Avro's schema](https://avro.apache.org/docs/1.11.1/specification/) that I initially
looked at and shied away from other schemas I need to look at for commonality and
interoperability are [Json Schema](https://json-schema.org/) and [Smooks Open API](https://swagger.io/resources/open-api/).


## Big Data Streaming Frameworks

ETL at scale, these all need clusters which is big setup and not really intended for
a bit of simple data transformation. I come back to them now and again but I 
don't really think they are comparable. They include [Apache Storm](https://storm.apache.org),
 [Apache Beam](https://beam.apache.org/), [ApacheFlink](https://flink.apache.org/), [KafkaStreams](https://kafka.apache.org/documentation/streams/)


## Random Links to come back to

- https://github.com/manuzhang/awesome-streaming
- https://github.com/pditommaso/awesome-pipeline
- https://github.com/pawl/awesome-etl

# Conclusion

These still isn't something quite like Dido and especially Dido
in Oddjob. It's worth pursuing - and anyway, writing it is Fun.
