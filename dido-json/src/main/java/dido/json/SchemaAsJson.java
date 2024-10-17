package dido.json;

import dido.data.ArrayDataDataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DataSchemaSchema;
import dido.data.DidoData;
import dido.how.CloseableConsumer;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.util.ClassUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class SchemaAsJson {

    public static DataSchema schemaFromData(DidoData data) {

        return DataSchemaSchema.schemaFromData(data, className -> {
            try {
                return ClassUtils.classFor(className, SchemaAsJson.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static DataSchema fromJson(InputStream input) throws Exception {

        DataInHow<InputStream> inHow = StreamInJson.asCopy(new ArrayDataDataFactoryProvider())
                .setSchema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
                .make();

        try (DataIn in = inHow.inFrom(input)) {

            DidoData data = in.stream()
                    .findFirst().orElseThrow(() -> new IOException("No Data in Input") );

            return schemaFromData(data);
        }
    }

    public static DataSchema fromJson(String string) throws Exception {

        return fromJson(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static void toJson(DataSchema schema, OutputStream output) throws Exception {

        try (DataOut out = StreamOutJson.streamOutSingle().outTo(output)) {

            DidoData data = DataSchemaSchema.schemaToData(schema);

            out.accept(data);
        }
    }

    public static String toJson(DataSchema schema) throws Exception {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        toJson(schema, output);
        return output.toString(StandardCharsets.UTF_8);
    }

    public static Stream<DataSchema> fromJsonStream(InputStream input) {

        DataInHow<InputStream> inHow = StreamInJsonLines.asWrapper()
                .setSchema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
                .make();

        DataIn in = inHow.inFrom(input);

        return in.stream()
                .map(SchemaAsJson::schemaFromData);
    }

    public static CloseableConsumer<DataSchema> toJsonStream(OutputStream output) {

        DataOut out = new StreamOutJsonLines().outTo(output);

        return new CloseableConsumer<>() {

            @Override
            public void close() {
                out.close();
            }

            @Override
            public void accept(DataSchema schema) {
                DidoData data = DataSchemaSchema.schemaToData(schema);

                out.accept(data);
            }

            @Override
            public String toString() {
                return "SchemaToJsonStream: " + output;
            }
        };
    }


}
