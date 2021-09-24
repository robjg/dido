package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.pickles.DataIn;
import dido.pickles.StreamIn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StreamInJson implements StreamIn<String> {

    private final DataSchema<String> schema;

    private final boolean partialSchema;

    public StreamInJson() {
        this(null, true);
    }

    public StreamInJson(DataSchema<String> schema, boolean partialSchema) {
        this.partialSchema = partialSchema || schema == null;
        this.schema = schema == null ? DataSchema.emptyStringFieldSchema() : schema;
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
        try {
            reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }

        reader.beginArray();

        return new DataIn<String>() {

            @Override
            public GenericData<String> get() {
                try {
                    if (reader.hasNext()) {
                        GenericData<String> genericData = gson.fromJson(reader, GenericData.class);
                        return genericData;
                    }
                    else {
                        return null;
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void close() {
                try {
                    reader.endArray();
                    reader.close();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }


}
