package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Objects;

/**
 * Provides the ability to Read in JSON from an Input Stream.
 * Delegates to either {@link JsonDataCopy}, {@link JsonDataPartialCopy} or {@link JsonDataWrapper}.
 * A Schema is required if a Wrapper is to be used as we'd need to cache the stream to work out the schema
 * which is just too complicated when a copy is available instead.
 *
 */
public class DataInJsonReader implements DataInHow<Reader> {

    private final Gson gson;

    private final boolean isArray;

    private DataInJsonReader(Gson gson, boolean isArray) {
        this.gson = gson;
        this.isArray = isArray;
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

    public static CopySettings asCopy() {

        return asCopy(DataFactoryProvider.newInstance());
    }

    public static CopySettings asCopy(DataFactoryProvider dataFactoryProvider) {

        return new CopySettings(dataFactoryProvider);
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

        public DataInHow<Reader> make(GsonBuilder gsonBuilder) {

            return new DataInJsonReader(
                    JsonDataWrapper.registerSchema(gsonBuilder, schema)
                            .create(),
                    isArray);
        }
    }

    public static class CopySettings {

        private final DataFactoryProvider dataFactoryProvider;

        private DataSchema schema;

        private boolean partial;

        private boolean isArray;

        CopySettings(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = Objects.requireNonNull(dataFactoryProvider);
        }

        public CopySettings setSchema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public CopySettings setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public CopySettings setIsArray(boolean isArray) {
            this.isArray = isArray;
            return this;
        }

        public DataInHow<Reader> make(GsonBuilder gsonBuilder) {

            if (schema == null || partial) {

                return new DataInJsonReader(
                        JsonDataPartialCopy.registerPartialSchema(gsonBuilder,
                                        schema)
                                .create(),
                        isArray);
            } else {

                return new DataInJsonReader(
                        JsonDataCopy.registerSchema(gsonBuilder,
                                        schema, dataFactoryProvider)
                                .create(),
                        isArray);
            }
        }
    }


    @Override
    public Class<Reader> getInType() {
        return Reader.class;
    }

    @Override
    public DataIn inFrom(Reader inFrom)  {

        final JsonReader reader = new JsonReader(inFrom);
        reader.setStrictness(Strictness.LENIENT);

        if (isArray) {
            try {
                reader.beginArray();
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        return new DataIn() {

            @Override
            public Iterator<DidoData> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        try {
                            return reader.hasNext();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }

                    @Override
                    public DidoData next() {
                        return gson.fromJson(reader, DidoData.class);
                    }
                };
            }

            @Override
            public void close() throws DataException {
                try (reader) {
                    if (isArray) {
                        reader.endArray();
                    }
                } catch (IOException e) {
                    throw DataException.of(e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "Stream In Json" + (isArray ? " from Array" : "");
    }
}
