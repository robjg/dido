package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dido.data.*;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StreamInJson<D extends DidoData> implements DataInHow<InputStream, D> {

    private final Gson gson;

    private final boolean isArray;

    private StreamInJson(Gson gson, boolean isArray) {
        this.gson = gson;
        this.isArray = isArray;
    }

    public static DataInHow<InputStream, NamedData> asCopyWithPartialSchema(DataSchema partialSchema,
                                                                            boolean isArray) {

        return asCopyWithPartialSchema(partialSchema, isArray, new ArrayDataDataFactoryProvider());
    }

    public static <D extends DidoData>
    DataInHow<InputStream, D> asCopyWithPartialSchema(DataSchema partialSchema,
                                                      boolean isArray,
                                                      DataFactoryProvider<D> dataFactoryProvider) {

        return new StreamInJson<>(JsonDataPartialCopy
                .registerPartialSchema(new GsonBuilder(), partialSchema, dataFactoryProvider)
                .create(), isArray);
    }

    public static DataInHow<InputStream, NamedData> asCopyWithSchema(DataSchema schema,
                                                                     boolean isArray) {

        return asCopyWithSchema(schema, isArray, new ArrayDataDataFactoryProvider());
    }

    public static <D extends DidoData>
    DataInHow<InputStream, D> asCopyWithSchema(DataSchema schema,
                                               boolean isArray,
                                               DataFactoryProvider<D> dataFactoryProvider) {

        return new StreamInJson<>(
                JsonDataCopy.registerSchema(new GsonBuilder(), schema, dataFactoryProvider.provideFactory(schema))
                        .create(), isArray);
    }

    /**
     * Stream JSON by wrapping the underlying data. Only {@link NamedData} is supported.
     *
     * @param schema  the full schema of the resultant data.
     * @param isArray is the stream an array or individual messages.
     * @return A How.
     */
    public static DataInHow<InputStream, NamedData> asWrapperWithSchema(DataSchema schema,
                                                                        boolean isArray) {
        return new StreamInJson<>(
                JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                        .create(), isArray);
    }

    public static Settings settings() {

        return new Settings();
    }

    public static class Settings {

        private DataSchema schema;

        private boolean partial;

        private boolean copy;

        private boolean isArray;


        public Settings setSchema(DataSchema schema) {
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

        public DataInHow<InputStream, NamedData> make() {

            if (schema == null || partial) {
                return asCopyWithPartialSchema(schema, isArray);
            } else {
                if (copy) {
                    return asCopyWithSchema(schema, isArray);

                } else {
                    return asWrapperWithSchema(schema, isArray);
                }
            }
        }

        public <D extends DidoData> DataInHow<InputStream, D> make(DataFactoryProvider<D> dataFactoryProvider) {

            if (schema == null || partial) {
                return asCopyWithPartialSchema(schema, isArray, dataFactoryProvider);
            } else {
                return asCopyWithSchema(schema, isArray, dataFactoryProvider);
            }
        }
    }


    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<D> inFrom(InputStream inputStream) throws IOException {

        final JsonReader reader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        if (isArray) {
            reader.beginArray();
        }

        return new DataIn<>() {

            @Override
            public D get() {
                try {
                    if (reader.hasNext()) {
                        return gson.fromJson(reader, NamedData.class);
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
