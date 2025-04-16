package dido.json;

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

        return fromJson(new InputStreamReader(input));
    }

    public static DataSchema fromJson(Reader input) throws Exception {

        DataInHow<Reader> inHow = DataInJson.with()
                .schema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
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

    public static void toJson(DataSchema schema, OutputStream output) {

        toJson(schema, new OutputStreamWriter(output));
    }

    public static void toJson(DataSchema schema, Writer output) {

        DidoData data = DataSchemaSchema.schemaToData(schema);

        try (DataOut out = DataOutJson.with()
                .schema(data.getSchema())
             .toAppendable(output)) {

            out.accept(data);
        }
    }

    public static String toJson(DataSchema schema) {

        StringWriter output = new StringWriter();
        toJson(schema, output);
        return output.toString();
    }

    public static Stream<DataSchema> fromJsonLines(InputStream input) {
        return fromJsonLines(new InputStreamReader(input));
    }

    public static Stream<DataSchema> fromJsonLines(Reader input) {

        DataInHow<Reader> inHow = DataInJson.with()
                .inFormat(JsonDidoFormat.LINES)
                .schema(DataSchemaSchema.DATA_SCHEMA_SCHEMA)
                .make();

        DataIn in = inHow.inFrom(input);

        return in.stream()
                .map(SchemaAsJson::schemaFromData);
    }

    public static CloseableConsumer<DataSchema> toJsonStream(OutputStream output) {

        return toJsonWriter(new OutputStreamWriter(output));
    }

    public static CloseableConsumer<DataSchema> toJsonWriter(Writer output) {

        DataOut out = DataOutJson.with()
                .outFormat(JsonDidoFormat.LINES)
                .toWriter(output);

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
