dido-oddball
============

Jobs and Types for using Dido in Oddjob.


- [**dido:data-in**](src/main/java/dido/oddjob/beanbus/DataInDriver.java)  
A Bus Driver for Bean Bus that Reads from an input converts it to Generic Data and
sends it to a destination.
- [**dido:data-out**](src/main/java/dido/oddjob/beanbus/DataOutDestination.java)  
A Service for Bean Bus that is a destination that accepts Generic Data and writes
it to an output.


- [**dido:schema**](src/main/java/dido/oddjob/schema/SchemaBean.java)  
A type that allow the definition of a Data Schema.


- [**dido:to-bean**](src/main/java/dido/oddjob/bean/ToBeanTransformer.java)  
Converts Generic Data to a Java Bean.
- [**dido:from-bean**](src/main/java/dido/oddjob/bean/FromBeanTransformer.java)
Converts a Java Bean to Generic Data.


- [**dido:transpose**](src/main/java/dido/oddjob/transpose/Transpose.java)  
Converts data to a new Data by copying fields and applying a function 
or setting new fields.

