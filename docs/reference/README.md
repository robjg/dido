# Oddjob Reference

### Jobs

- [dido:converter](dido/oddjob/util/DidoConverterJob.md) - Provides a `dido.how.conversion.DidoConversionProvider` using Oddjob's conversions.
- [dido:data-in](dido/oddjob/beanbus/DataInDriver.md) - A Bean Bus Driver that reads data from the 'from' according to the given 'how'.
- [dido:data-out](dido/oddjob/beanbus/DataOutDestination.md) - A Bean Bus Destination that accepts `dido.data.DidoData` and writes it out to the given 'to' according to the given 'how'.
- [dido:stream-join](dido/operators/join/StreamJoinService.md) - A service that Joins two sources of `dido.data.DidoData` into a single destination.

### Types

- [dido:copy](dido/operators/transform/ValueCopyFactory.md) - 
- [dido:csv](dido/csv/CsvDido.md) - Creates an In or Out for CSV Data.
- [dido:field](dido/oddjob/schema/SchemaFieldBean.md) - Define the field of a Schema.
- [dido:flatten](dido/operators/FlattenType.md) - 
- [dido:from-bean](dido/oddjob/bean/FromBeanTransformer.md) - Transform a Bean to Dido Data.
- [dido:from-json](dido/json/FromJsonStringType.md) - Provides a Mapping Function that will convert a GSON String into Dido Data.
- [dido:json](dido/json/JsonDido.md) - Creates an In or an Out for JSON data.
- [dido:left-join](dido/operators/join/LeftStreamJoinType.md) - A join operation that performs a full left join.
- [dido:lines](dido/oddjob/util/LinesDido.md) - Creates an In or Out for Lines of Text.
- [dido:schema](dido/oddjob/schema/SchemaBean.md) - Define a Schema.
- [dido:set](dido/operators/transform/ValueSetFactory.md) - Set the value for a field or index.
- [dido:sql](dido/sql/SqlDido.md) - Export and Import with SQL.
- [dido:table](dido/text/TextTableDido.md) - Creates an Out that write data to a text table.
- [dido:to-bean](dido/oddjob/bean/ToBeanTransformer.md) - 
- [dido:to-json](dido/json/ToJsonStringType.md) - Provides a Mapping Function that will convert a Dido Data into a JSON String.
- [dido:transform](dido/operators/transform/TransformationFactory.md) - 
- [dido-poi:blank](dido/poi/layouts/BlankCell.md) - Create a column cells that are blank.
- [dido-poi:boolean](dido/poi/layouts/BooleanCell.md) - Define a column of Boolean cells.
- [dido-poi:date](dido/poi/layouts/DateCell.md) - Define a date column.
- [dido-poi:numeric](dido/poi/layouts/NumericCell.md) - Define a number column.
- [dido-poi:numeric-formula](dido/poi/layouts/NumericFormulaCell.md) - Define a Numeric Formula column.
- [dido-poi:rows](dido/poi/layouts/DataRows.md) - Define an area in a spreadsheet sheet for reading and writing rows to.
- [dido-poi:style](dido/poi/style/StyleBean.md) - 
- [dido-poi:text](dido/poi/layouts/TextCell.md) - Define a text column.
- [dido-poi:text-formula](dido/poi/layouts/TextFormulaCell.md) - Define a Text Formula Column.
- [dido-poi:workbook](dido/poi/data/PoiWorkbook.md) - A source or sink of data that is a Microsoft Excel Spreadsheet.

-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
