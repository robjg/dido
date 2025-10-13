Dido CSV
========

Comma Separated Values in and out. [DataInCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataInCsv.html)
and [DataOutCsv](http://rgordon.co.uk/projects/dido/current/api/dido/csv/DataOutCsv.html)
in the module [dido-csv](../dido-csv) provide a wrapper around [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)

- [Overview](#overview)
- [With Schemas](#with-schemas)
- [Custom-Formats](#custom-formats)
- [Oddjob](#oddjob)

### Overview
We have already seen in the [README](../README.md) an example of Reading a CSV. By default,
all columns are Strings, and the field names are derived. In the last
example a Schema was provided that gave the columns names and types.

In [dido-sql](DIDO-SQL.md) we see that column names can also be taken from
the header of the CSV file, and a partial schema applied which only
overrides the type of specified columns leaving the rest as String.
We also see an Example of writing a CSV including a header line.

### With Schemas

If a schema is provided, and the CSV has a header, the schema field names are used to match the columns.
If there is no header, then the schema indices are used to pick the columns.  
```java
        DataSchema schema = DataSchema.builder()
                .addNamedAt(1,"Fruit", String.class)
                .addNamedAt(3, "Price", double.class)
                .build();

        DidoData data = DataInCsv.with()
                .schema(schema)
                .mapFromString()
                .apply("Apple, 5, 23.5");

        assertThat(data,
                is(DidoData.withSchema(schema).of("Apple", 23.5)));
```

Indices are not used when writing data. Just the order of the fields in the schema is important.
```java
        String csv = DataOutCsv.with()
                .schema(schema)
                .mapToString()
                .apply(data);

        assertThat(csv,
                is("Apple,23.5"));
```

Adding blank columns can be achieved by using a Transformer. 
```java
        DidoTransform transform = ViewTransformBuilder
                .with()
                .existingFields(true)
                .forSchema(data.getSchema())
                .addFieldView(FieldViews.setNamedAt(2, "Qty", null, int.class))
                .build();

        String csv2 = DataOutCsv.with()
                .schema(transform.getResultantSchema())
                .mapToString()
                .apply(transform.apply(data));

        assertThat(csv2,
                is("Apple,,23.5"));
```


### Custom Formats

To customise the format, provide a [CSVFormat](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html)
as a setting.

When writing:
```java
        DataSchema schema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Qty", int.class)
                .addNamed("Price", double.class)
                .build();

        List<DidoData> didoData = DidoData.withSchema(schema)
                .many()
                .of("Apple", "5", "19.50")
                .of("Orange", "2", "35.24")
                .of("Pear", "3", "26.84")
                .toList();

        Writer writer = new StringWriter();

        try (DataOut out = DataOutCsv.with()
                .csvFormat(CSVFormat.DEFAULT.builder()
                        .setDelimiter('|')
                        .setRecordSeparator('\n')
                        .get())
                .toWriter(writer)) {

            didoData.forEach(out);
        }

        assertThat(writer.toString(),
                is("""
                        Apple|5|19.50
                        Orange|2|35.24
                        Pear|3|26.84
                        """));
```

When reading:
```java
        Reader reader = new StringReader(writer.toString());

        List<DidoData> dataBack;

        try (DataIn in = DataInCsv.with()
                .csvFormat(CSVFormat.DEFAULT.builder()
                        .setDelimiter('|')
                        .setRecordSeparator('\n')
                        .get())
                .fromReader(reader)) {

            dataBack = in.stream().toList();
        }

        assertThat(dataBack, contains(
                DidoData.of("Apple", "5", "19.50"),
                DidoData.of("Orange", "2", "35.24"),
                DidoData.of("Pear", "3", "26.84")));
```


### Oddjob

For examples of using Dido CSV in Oddjob, see [dido:csv](reference/dido/csv/CsvDido.md)
