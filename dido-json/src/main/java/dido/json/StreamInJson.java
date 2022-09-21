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

    private final Gson gson;

    private final boolean isArray;

    private StreamInJson(Gson gson, boolean isArray) {
        this.gson = gson;
        this.isArray = isArray;
    }

    public static DataInHow<String, InputStream> asCopyWithPartialSchema(DataSchema<String> partialSchema,
                                                                         boolean isArray) {
        return new StreamInJson(JsonDataPartialCopy
                .registerPartialSchema(new GsonBuilder(), partialSchema)
                .create(), isArray);
    }

    public static DataInHow<String, InputStream> asCopyWithSchema(DataSchema<String> partialSchema,
                                                                         boolean isArray) {
        return new StreamInJson(JsonDataCopy
                .registerSchema(new GsonBuilder(), partialSchema)
                .create(), isArray);
    }

    public static DataInHow<String, InputStream> asWrapperWithSchema(DataSchema<String> schema,
                                                                  boolean isArray) {
        return new StreamInJson(JsonDataWrapper
                .registerSchema(new GsonBuilder(), schema)
                .create(), isArray);
    }

    public static Settings settings() {

        return new Settings();
    }

    public static class Settings {

        private DataSchema<String> schema;

        private boolean partial;

        private boolean copy;

        private boolean isArray;


        public Settings setSchema(DataSchema<String> schema) {
            this.schema = schema;
            return this;
        }

        public Settings setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public Settings setIsArray(boolean isArray) {
            this.isArray = isArray;
            return this;
        }

        public Settings setCopy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public DataInHow<String, InputStream> make() {

            if (schema == null || partial) {
                return asCopyWithPartialSchema(schema, isArray);
            }
            else {
                if (copy) {
                    return asCopyWithSchema(schema, isArray);

                }
                else {
                    return asWrapperWithSchema(schema, isArray);
                }
            }
        }
    }


    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<String> inFrom(InputStream inputStream) throws IOException {

        final JsonReader reader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        if (isArray) {
            reader.beginArray();
        }

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
                if (isArray) {
                    reader.endArray();
                }
                reader.close();
            }
        };
    }

    @Override
    public String toString() {
        return "Stream In Json" + (isArray ? " from Array" : "");
    }
}
