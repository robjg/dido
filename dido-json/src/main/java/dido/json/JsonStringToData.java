package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.MapperFrom;

import java.util.function.Function;

/**
 * Internal implementation for {@link DataInJson#mapFromString()}. Note that the copy
 * with {@link JsonDataCopy} is no longer used from {@link DataInJson},
 * only {@link JsonDataPartialCopy} is.
 */
public class JsonStringToData {

    public static Function<String, DidoData> asWrapperWithSchema(DataSchema schema) {

        JsonDataWrapper jsonDataWrapper = JsonDataWrapper.forSchema(schema);

        return new Known(jsonDataWrapper.init(new GsonBuilder()).create(),
                jsonDataWrapper.getSchema(),
                "ToWrapper, schema=" + schema);
    }

    public static WrapperSettings asWrapper() {
        return new WrapperSettings();
    }

    public static CopySettings asCopy() {
        return asCopy(DataFactoryProvider.newInstance());
    }

    public static CopySettings asCopy(DataFactoryProvider dataFactoryProvider) {

        return new CopySettings(dataFactoryProvider);
    }

    public static class WrapperSettings {

        private DataSchema schema;

        private boolean partial;

        public WrapperSettings setSchema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public WrapperSettings setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public Function<String, DidoData> make(GsonBuilder gsonBuilder) {

            if (schema == null || partial) {

                return new UnknownWrapper(gsonBuilder,
                        schema == null ? DataSchema.emptySchema() : schema);
            } else {

                JsonDataWrapper jsonDataWrapper = JsonDataWrapper.forSchema(schema);

                return new Known(jsonDataWrapper.init(gsonBuilder).create(),
                        jsonDataWrapper.getSchema(),
                        "ToWrapper, schema=" + schema);
            }
        }
    }

    public static class CopySettings {

        private final DataFactoryProvider dataFactoryProvider;

        private DataSchema schema;

        private boolean partial;

        public CopySettings(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public CopySettings setSchema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public CopySettings setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public Function<String, DidoData> make(GsonBuilder gsonBuilder) {

            if (schema == null || partial) {
                DataSchema partialSchema = schema == null ? DataSchema.emptySchema() : schema;

                return new UnKnownCopy(
                        JsonDataPartialCopy.registerPartialSchema(
                                        gsonBuilder, partialSchema)
                                .create(),
                        "toCopy, partialSchema=" + partialSchema);
            } else {

                JsonDataCopy jsonDataCopy = JsonDataCopy.registerSchema(schema,
                        dataFactoryProvider);

                return new Known(jsonDataCopy.init(gsonBuilder).create(),
                        jsonDataCopy.getSchema(),
                        "ToCopy, schema=" + schema);
            }
        }
    }

    static class UnknownWrapper implements Function<String, DidoData> {

        private final GsonBuilder gsonBuilder;

        private final DataSchema partialSchema;

        private volatile Known known;

        UnknownWrapper(GsonBuilder gsonBuilder,
                       DataSchema partialSchema) {
            this.gsonBuilder = gsonBuilder;
            this.partialSchema = partialSchema;
        }

        @Override
        public DidoData apply(String s) {
            if (known == null) {
                DataSchema schema = JsonSchemaExtractor
                        .registerPartialSchema(gsonBuilder.create().newBuilder(), partialSchema)
                        .create()
                        .fromJson(s, DataSchema.class);

                JsonDataWrapper jsonDataWrapper = JsonDataWrapper.forSchema(schema);
                Gson gson = jsonDataWrapper.init(gsonBuilder.create().newBuilder()).create();

                known = new Known(gson, jsonDataWrapper.getSchema(),
                        toString());
            }
            return known.apply(s);
        }

        @Override
        public String toString() {
            return "ToWrapper, partialSchema=" + partialSchema;
        }
    }

    static class UnKnownCopy implements Function<String, DidoData> {

        private final Gson gson;

        private final String toString;

        UnKnownCopy(Gson gson,
              String toString) {
            this.gson = gson;
            this.toString = toString;
        }

        @Override
        public DidoData apply(String s) {
            return gson.fromJson(s, DidoData.class);
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    static class Known implements MapperFrom<String> {

        private final Gson gson;

        private final DataSchema schema;

        private final String toString;

        Known(Gson gson,
              DataSchema schema,
              String toString) {
            this.gson = gson;
            this.schema = schema;
            this.toString = toString;
        }

        @Override
        public DataSchema getSchema() {
            return schema;
        }

        @Override
        public DidoData apply(String s) {
            return gson.fromJson(s, DidoData.class);
        }

        @Override
        public String toString() {
            return toString;
        }
    }

}
