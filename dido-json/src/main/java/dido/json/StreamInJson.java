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
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides the ability to Read in JSON from an Input Stream.
 * Delegates to either {@link JsonDataCopy}, {@link JsonDataPartialCopy} or {@link JsonDataWrapper}.
 * A Schema is required if a Wrapper is to be used as we'd need to cache the stream to work out the schema
 * which is just too complicated when a copy is available instead.
 *
 * @param <D> The type of Data that will be produced.
 */
public class StreamInJson<D extends DidoData> implements DataInHow<InputStream, D> {

    private final Gson gson;

    private final boolean isArray;

    private final Type dataType;

    private StreamInJson(Gson gson, boolean isArray, Type dataType) {
        this.gson = gson;
        this.isArray = isArray;
        this.dataType = dataType;
    }

    /**
     * Stream JSON by wrapping the underlying data. Only {@link DidoData} is supported.
     *
     * @param schema  the full schema of the resultant data.
     * @return Settings.
     */
    public static WrapperSettings asWrapper(DataSchema schema) {

        return new WrapperSettings(schema);
    }

    public static CopySettings<ArrayData> asCopy() {

        return new CopySettings<>(new ArrayDataDataFactoryProvider());
    }

    public static <D extends DidoData> CopySettings<D> asCopy(DataFactoryProvider<D> dataFactoryProvider) {

        return new CopySettings<>(dataFactoryProvider);
    }

    public static class WrapperSettings {

        private final DataSchema schema;

        private boolean isArray;

        WrapperSettings(DataSchema schema) {
            this.schema = Objects.requireNonNull(schema, "Schema required for a Stream Wrapper");
        }

        public WrapperSettings setIsArray(boolean isArray) {
            this.isArray = isArray;
            return this;
        }

        public DataInHow<InputStream, DidoData> make() {

            return new StreamInJson<>(
                    JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                            .create(),
                    isArray,
                    DidoData.class);
        }
    }

    public static class CopySettings<D extends DidoData> {

        private final DataFactoryProvider<D> dataFactoryProvider;

        private DataSchema schema;

        private boolean partial;

        private boolean isArray;

        CopySettings(DataFactoryProvider<D> dataFactoryProvider) {
            this.dataFactoryProvider = Objects.requireNonNull(dataFactoryProvider);
        }

        public CopySettings<D> setSchema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public CopySettings<D> setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public CopySettings<D> setIsArray(boolean isArray) {
            this.isArray = isArray;
            return this;
        }


        public DataInHow<InputStream, D> make() {

            if (schema == null || partial) {

                return new StreamInJson<>(
                        JsonDataPartialCopy.registerPartialSchema(new GsonBuilder(), schema, dataFactoryProvider)
                                .create(),
                        isArray,
                        dataFactoryProvider.getDataType());
            } else {

                return new StreamInJson<>(
                        JsonDataCopy.registerSchema(new GsonBuilder(),
                                        schema, dataFactoryProvider)
                                .create(),
                        isArray,
                        dataFactoryProvider.getDataType());
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
                        return gson.fromJson(reader, dataType);
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
