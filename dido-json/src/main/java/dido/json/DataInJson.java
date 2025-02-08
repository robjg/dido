package dido.json;

import com.google.gson.GsonBuilder;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.conversion.DidoConversionProvider;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * How to read JSON Data In.
 */
public class DataInJson implements DataInHow<Reader> {

    private final DataInHow<Reader> delegate;

    public static class Settings {

        private JsonDidoFormat didoFormat;

        private DataSchema schema;

        private boolean partialSchema;

        private final GsonBuilder gsonBuilder = new GsonBuilder();

        private final DidoConversionAdaptorFactory.Settings didoConversion =
                DidoConversionAdaptorFactory.with();

        public Settings inFormat(JsonDidoFormat didoFormat) {
            this.didoFormat = didoFormat;
            return this;
        }

        public Settings gsonBuilder(Consumer<? super GsonBuilder> withBuilder) {
            withBuilder.accept(gsonBuilder);
            return this;
        }

        public Settings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings partialSchema(boolean partialSchema) {
            this.partialSchema = partialSchema;
            return this;
        }

        public Settings partialSchema(DataSchema schema) {
            this.schema = schema;
            this.partialSchema = true;
            return this;
        }

        public Settings conversionProvider(DidoConversionProvider conversionProvider) {
            didoConversion.conversionProvider(conversionProvider);
            return this;
        }

        public Settings didoConversion(Type from, Type to) {
            didoConversion.register(from, to);
            return this;
        }

        public DataIn fromPath(Path path) {
            try {
                return make().inFrom(Files.newBufferedReader(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataIn fromInputStream(InputStream inputStream) {
            return make().inFrom(new BufferedReader(new InputStreamReader(inputStream)));
        }

        public DataIn fromReader(Reader reader) {
            return make().inFrom(reader);
        }

        private void registerGsonBuilderDefaults() {

            DidoConversionAdaptorFactory didoConversionAdaptorFactory = didoConversion.make();
            if (!didoConversionAdaptorFactory.isEmpty()) {
                gsonBuilder.registerTypeAdapterFactory(didoConversionAdaptorFactory);
            }
        }

        public DataInJson make() {

            registerGsonBuilderDefaults();

            if (didoFormat == JsonDidoFormat.LINES) {
                return new DataInJson(
                        DataInJsonLines.withFunction(mapFromString()));
            }
            else {
                if (schema == null || partialSchema) {
                    return new DataInJson(
                            DataInJsonReader.asCopy()
                                    .setSchema(schema)
                                    .setPartial(true)
                                    .setIsArray(didoFormat == JsonDidoFormat.ARRAY)
                                    .make(gsonBuilder));
                }
                else {
                    return new DataInJson(
                            DataInJsonReader.asWrapper(schema)
                                    .setIsArray(didoFormat == JsonDidoFormat.ARRAY)
                                    .make(gsonBuilder));
                }
            }
        }

        public Function<String, DidoData> mapFromString() {

            registerGsonBuilderDefaults();

            if (schema == null || partialSchema) {
                return JsonStringToData.asCopy()
                        .setSchema(schema)
                        .setPartial(true)
                        .make(gsonBuilder);
            }
            else {
                return JsonStringToData.asWrapper()
                        .setSchema(schema)
                        .make(gsonBuilder);
            }
        }
    }

    private DataInJson(DataInHow<Reader> delegate) {
        this.delegate = delegate;
    }

    public static DataIn fromPath(Path path) {

        return with().fromPath(path);
    }

    public static DataIn fromReader(Reader reader) {

        return with().fromReader(reader);
    }

    public static DataIn fromInputStream(InputStream inputStream) {

        return with().fromInputStream(inputStream);
    }

    public static Function<String, DidoData> mapFromString() {

        return with().mapFromString();
    }

    public static Settings with() {
        return new Settings();
    }

    @Override
    public Class<Reader> getInType() {
        return Reader.class;
    }

    @Override
    public DataIn inFrom(Reader reader) {

        return delegate.inFrom(reader);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
