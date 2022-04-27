package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StreamInJson implements DataInHow<String, InputStream> {

    private final DataSchema<String> schema;

    private final boolean partialSchema;

    public StreamInJson() {
        this(null, true);
    }

    public StreamInJson(DataSchema<String> schema, boolean partialSchema) {
        this.partialSchema = partialSchema || schema == null;
        this.schema = schema == null ? DataSchema.emptySchema() : schema;
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<String> inFrom(InputStream inputStream) throws IOException {

        Gson gson = new GsonBuilder().registerTypeAdapter(GenericData.class,
                        new FieldRecordDeserializer(schema, partialSchema))
                .create();

        JsonReader reader;
        reader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        reader.beginArray();

        return new DataIn<>() {

            @Override
            public GenericData<String> get() {
                try {
                    if (reader.hasNext()) {
                        return gson.fromJson(reader, GenericData.class);
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void close() throws IOException {
                reader.endArray();
                reader.close();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("JsonArray");
        if (this.schema != null) {
            if (partialSchema) {
                builder.append(", with partial schema");
            } else {
                builder.append(", with schema");
            }
        } else {
            builder.append(", with no schema");
        }
        return builder.toString();
    }
}
