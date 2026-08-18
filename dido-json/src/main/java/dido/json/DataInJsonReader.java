package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.util.OneAheadIterator;

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

    private final DataSchema schema;

    private final boolean isArray;

    private DataInJsonReader(Gson gson,
                             DataSchema schema,
                             boolean isArray) {
        this.gson = gson;
        this.schema = schema;
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

            JsonDataWrapper jsonDataWrapper = JsonDataWrapper.forSchema(schema);

            return new DataInJsonReader(
                    jsonDataWrapper.init(gsonBuilder).create(),
                    jsonDataWrapper.getSchema(),
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
                        null,
                        isArray);
            } else {

                JsonDataCopy jsonDataCopy = JsonDataCopy.registerSchema(schema, dataFactoryProvider);

                return new DataInJsonReader(
                        jsonDataCopy.init(gsonBuilder).create(),
                        jsonDataCopy.getSchema(),
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

        final JsonReader reader = gson.newJsonReader(inFrom);

        if (isArray) {
            try {
                reader.beginArray();
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        Iterator<DidoData> originalIterator = new Iterator<>() {

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

        Iterator<DidoData> iterator;
        DataSchema schema;

        if (this.schema == null) {

            if (originalIterator.hasNext()) {

                DidoData didoData = originalIterator.next();
                iterator = new OneAheadIterator<>(originalIterator, didoData);
                schema = didoData.getSchema();
            }
            else {
                return DataIn.empty();
            }
        }
        else {
            iterator = originalIterator;
            schema = this.schema;
        }

        return new DataIn() {

            @Override
            public DataSchema getSchema() {
                return schema;
            }

            @Override
            public Iterator<DidoData> iterator() {
                return iterator;
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
