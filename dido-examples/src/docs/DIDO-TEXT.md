Dido Text
========

Out to an Ascii Text Table. [DataOutTextTable](http://rgordon.co.uk/projects/dido/current/api/dido/text/DataOutTextTable.html)
in the module [dido-text](dido-text) provide a wrapper around [org.nocrala.tools.texttablefmt/text-table-formatter](https://mvnrepository.com/artifact/org.nocrala.tools.texttablefmt/text-table-formatter)

Here's an example of writing. Given this schema and data:
{@oddjob.java.file src/test/java/dido/examples/TextExamplesTest.java#snippet1}
We can write it to a text table with:
{@oddjob.java.file src/test/java/dido/examples/TextExamplesTest.java#snippet2}
Giving this result:
{@oddjob.text.file  src/test/resources/examples/Fruit.txt}


### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:table](docs/reference/dido/text/TextTableDido.md)
