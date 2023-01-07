package dido.json;

import dido.data.DataSchema;
import dido.data.DataSchemaSchema;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.util.ClassUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class SchemaAsJson {


    public static DataSchema<String> fromJson(InputStream input) throws Exception {

        DataInHow<String, InputStream> inHow = StreamInJson.settings()
                .setSchema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
                .make();

        try (DataIn<String> in = inHow.inFrom(input)) {

            GenericData<String> data = in.get();

            return DataSchemaSchema.schemaFromData(data, className -> {
                try {
                    return ClassUtils.classFor(className, SchemaAsJson.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    public static void toJson(DataSchema<String> schema, OutputStream output) throws Exception {

        try (DataOut<String> out = StreamOutJson.streamOutSingle().outTo(output)) {

            GenericData<String> data = DataSchemaSchema.schemaToData(schema);

            out.accept(data);
        }
    }
}
