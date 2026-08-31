# Conversions

Conversion documentation is a Work in Progress.

| From | To | Description |
| -------- | ----------- | ----------- |
| dido.csv.CsvDataInBean | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.csv.CsvDataInBean | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.csv.CsvDataOutBean | dido.how.RefinableFunction<dido.data.DidoData, java.lang.String> |  | 
| dido.csv.CsvDataOutBean | dido.how.RefinableOutHow<java.io.OutputStream> |  | 
| dido.csv.CsvFormatType | org.apache.commons.csv.CSVFormat |  | 
| dido.json.JsonDataInBean | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.json.JsonDataInBean | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.json.JsonDataOutBean | dido.how.RefinableOutHow<java.io.OutputStream> |  | 
| dido.json.JsonDataOutBean | java.util.function.Function<dido.data.DidoData, java.lang.String> |  | 
| dido.oddjob.bean.FromBeanTransformer | java.util.function.Function |  | 
| dido.oddjob.bean.ToBeanTransformer | java.util.function.Function |  | 
| dido.oddjob.schema.SchemaBean | dido.data.DataSchema |  | 
| dido.oddjob.schema.SchemaBean | dido.oddjob.schema.NestedSchema |  | 
| dido.oddjob.schema.SchemaFromBean | dido.data.DataSchema |  | 
| dido.oddjob.schema.SchemaFromBean | dido.oddjob.schema.NestedSchema |  | 
| dido.oddjob.util.LinesInBean | dido.how.DataInHow<java.io.InputStream> |  | 
| dido.oddjob.util.LinesInBean | java.util.function.Function<java.lang.String, dido.data.DidoData> |  | 
| dido.oddjob.util.LinesOutBean | dido.how.RefinableOutHow<java.io.OutputStream> |  | 
| dido.oddjob.util.LinesOutBean | java.util.function.Function<dido.data.DidoData, java.lang.String> |  | 
| dido.operators.FlattenType | java.util.function.Function<dido.data.DidoData, java.util.List<dido.data.DidoData>> |  | 
| dido.operators.join.LeftStreamJoinType | dido.operators.join.StreamJoin |  | 
| dido.operators.transform.TransformationFactory | java.util.function.Function<dido.data.DidoData, dido.data.DidoData> |  | 
| dido.operators.transform.ValueCopyFactory | dido.operators.transform.FieldView |  | 
| dido.operators.transform.ValueRemoveFactory | dido.operators.transform.FieldView |  | 
| dido.operators.transform.ValueSetFactory | dido.operators.transform.FieldView |  | 
| dido.sql.SqlDido | dido.how.DataInHow<java.sql.Connection> |  | 
| dido.sql.SqlDido | dido.how.DataOutHow<java.sql.Connection> |  | 
| dido.text.TextTableDido | dido.how.DataOutHow<java.io.OutputStream> |  | 

