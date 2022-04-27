package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class StreamInJsonLines implements DataInHow<String, InputStream> {

    private static final Type stringGenericDataType =
            new TypeToken<GenericData<String>>() {}.getType();

    private final DataSchema<String> schema;

    private final boolean partialSchema;

    public StreamInJsonLines() {
        this(null, true);
    }

    public StreamInJsonLines(DataSchema<String> schema, boolean partialSchema) {
        this.partialSchema = partialSchema || schema == null;
        this.schema = schema == null ? DataSchema.emptySchema() : schema;
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<String> inFrom(InputStream inputStream) {

        Gson gson = new GsonBuilder().registerTypeAdapter(GenericData.class,
                        new FieldRecordDeserializer(schema, partialSchema))
                .create();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return new DataIn<>() {

            @Override
            public GenericData<String> get() {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        return null;
                    } else {
                        return gson.fromJson(line, stringGenericDataType);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("JsonLines");
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
