package dido.json;

import dido.data.DataSchema;
import dido.data.DataSchemaSchema;
import dido.data.GenericData;
import dido.how.*;
import dido.how.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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

    public static DataSchema<String> fromJson(String string) throws Exception {

        return fromJson(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static void toJson(DataSchema<String> schema, OutputStream output) throws Exception {

        try (DataOut<String> out = StreamOutJson.streamOutSingle().outTo(output)) {

            GenericData<String> data = DataSchemaSchema.schemaToData(schema);

            out.accept(data);
        }
    }

    public static String toJson(DataSchema<String> schema) throws Exception {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        toJson(schema, output);
        return output.toString(StandardCharsets.UTF_8);
    }

    public static CloseableSupplier<DataSchema<String>> fromJsonStream(InputStream input) throws Exception {

        DataInHow<String, InputStream> inHow = StreamInJsonLines.settings()
                .setSchema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
                .make();

        DataIn<String> in = inHow.inFrom(input);

        return new CloseableSupplier<>() {

            @Override
            public void close() throws Exception {

                in.close();
            }

            @Override
            public DataSchema<String> get() {

                GenericData<String> data = in.get();

                if (data == null) {
                    return null;
                }

                return DataSchemaSchema.schemaFromData(data, className -> {
                    try {
                        return ClassUtils.classFor(className, SchemaAsJson.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public String toString() {
                return "SchemaFromJsonStream: " + input;
            }
        };
    }

    public static CloseableConsumer<DataSchema<String>> toJsonStream(OutputStream output) {

        DataOut<String> out = new StreamOutJsonLines().outTo(output);

        return new CloseableConsumer<>() {

            @Override
            public void close() throws Exception {
                out.close();
            }

            @Override
            public void accept(DataSchema<String> schema) {
                GenericData<String> data = DataSchemaSchema.schemaToData(schema);

                out.accept(data);
            }

            @Override
            public String toString() {
                return "SchemaToJsonStream: " + output;
            }
        };
    }


}
