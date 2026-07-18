# Conversions

Conversion documentation is a Work in Progress.

| From | To | Description |
| -------- | ----------- | ----------- |
| dido.csv.CsvDido | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.csv.CsvDido | dido.how.DataOutHow<java.io.OutputStream> |  | 
| dido.csv.CsvFormatType | org.apache.commons.csv.CSVFormat |  | 
| dido.json.FromJsonStringType | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.json.JsonDido | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.json.JsonDido | dido.how.DataOutHow<java.io.OutputStream> |  | 
| dido.json.JsonDido | java.util.function.Function<dido.data.DidoData, java.lang.String> |  | 
| dido.json.JsonDido | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.json.ToJsonStringType | java.util.function.Function<dido.data.DidoData, java.lang.String> |  | 
| dido.oddjob.bean.FromBeanTransformer | java.util.function.Function |  | 
| dido.oddjob.bean.ToBeanTransformer | java.util.function.Function |  | 
| dido.oddjob.schema.SchemaBean | dido.data.DataSchema |  | 
| dido.oddjob.schema.SchemaBean | dido.oddjob.schema.NestedSchema |  | 
| dido.oddjob.schema.SchemaFromBean | dido.data.DataSchema |  | 
| dido.oddjob.schema.SchemaFromBean | dido.oddjob.schema.NestedSchema |  | 
| dido.oddjob.util.LinesDido | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.oddjob.util.LinesDido | dido.how.DataOutHow<java.io.OutputStream> |  | 
| dido.oddjob.util.LinesDido | java.util.function.Function<dido.data.DidoData, java.lang.String> |  | 
| dido.oddjob.util.LinesDido | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.operators.FlattenType | java.util.function.Function<dido.data.DidoData, java.util.List<dido.data.DidoData>> |  | 
| dido.operators.join.LeftStreamJoinType | dido.operators.join.StreamJoin |  | 
| dido.operators.transform.TransformationFactory | java.util.function.Function<dido.data.DidoData, dido.data.DidoData> |  | 
| dido.operators.transform.ValueCopyFactory | dido.operators.transform.FieldView |  | 
| dido.operators.transform.ValueRemoveFactory | dido.operators.transform.FieldView |  | 
| dido.operators.transform.ValueSetFactory | dido.operators.transform.FieldView |  | 
| dido.sql.SqlDido | dido.how.DataInHow<java.sql.Connection> |  | 
| dido.sql.SqlDido | dido.how.DataOutHow<java.sql.Connection> |  | 
| dido.text.TextTableDido | dido.how.DataOutHow<java.io.OutputStream> |  | 

