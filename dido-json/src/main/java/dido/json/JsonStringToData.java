package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.function.Function;

public class JsonStringToData {

    public static Function<String, DidoData> asWrapperWithSchema(DataSchema schema) {

        return new Known(JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create(),
                "ToWrapper, schema=" + schema);
    }

    public static WrapperSettings asWrapper() {
        return new WrapperSettings();
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

                return new UnknownWrapper(schema == null ? DataSchema.emptySchema() : schema);
            } else {

                return new Known(JsonDataWrapper.registerSchema(gsonBuilder, schema)
                        .create(),
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

                return new Known(
                        JsonDataPartialCopy.registerPartialSchema(
                                        gsonBuilder, partialSchema, dataFactoryProvider)
                                .create(),
                        "toCopy, partialSchema=" + partialSchema);
            } else {

                return new Known(JsonDataCopy.registerSchema(gsonBuilder, schema,
                                dataFactoryProvider)
                        .create(),
                        "ToCopy, schema=" + schema);
            }
        }
    }

    static class UnknownWrapper implements Function<String, DidoData> {

        private final DataSchema partialSchema;

        private volatile Known known;

        UnknownWrapper(DataSchema partialSchema) {
            this.partialSchema = partialSchema;
        }

        @Override
        public DidoData apply(String s) {
            if (known == null) {
                DataSchema schema = JsonSchemaExtractor
                        .registerPartialSchema(new GsonBuilder(), partialSchema)
                        .create()
                        .fromJson(s, DataSchema.class);
                Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema).create();
                known = new Known(gson, toString());
            }
            return known.apply(s);
        }

        @Override
        public String toString() {
            return "ToWrapper, partialSchema=" + partialSchema;
        }
    }

    static class Known implements Function<String, DidoData> {

        private final Gson gson;

        private final String toString;

        Known(Gson gson, String toString) {
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

}
