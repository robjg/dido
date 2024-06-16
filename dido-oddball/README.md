dido-oddball
============

Jobs and Types for using Dido in Oddjob.


- [**dido:data-in**](src/main/java/dido/oddjob/beanbus/DataInDriver.java)  
A Bus Driver for Bean Bus that Reads from an input converts it to Dido Data and
sends it to a destination.
- [**dido:data-out**](src/main/java/dido/oddjob/beanbus/DataOutDestination.java)  
A Service for Bean Bus that is a destination that accepts Dido Data and writes
it to an output.


- [**dido:schema**](src/main/java/dido/oddjob/schema/SchemaBean.java)  
A type that allow the definition of a Data Schema.


- [**dido:to-bean**](src/main/java/dido/oddjob/bean/ToBeanTransformer.java)  
Converts Dido Data to a Java Bean.
- [**dido:from-bean**](src/main/java/dido/oddjob/bean/FromBeanTransformer.java)
Converts a Java Bean to Dido Data.


- [**dido:transform**](src/main/java/dido/oddjob/transform/Transform.java)  
Converts data to a new Data by copying fields and applying a function 
or setting new fields.

