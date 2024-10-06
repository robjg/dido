package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.NamedData;

import java.lang.reflect.Type;
import java.util.function.Function;

public class JsonStringToData {

    public static Function<String, NamedData> asWrapperWithSchema(DataSchema schema) {

        return new Known<>(JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create(),
                JsonDataWrapper.DATA_TYPE,
                "ToWrapper, schema=" + schema);
    }

    public static WrapperSettings asWrapper() {
        return new WrapperSettings();
    }

    public static <D extends DidoData> CopySettings<D> asCopy(DataFactoryProvider<D> dataFactoryProvider) {

        return new CopySettings<>(dataFactoryProvider);
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

        public Function<String, NamedData> make() {

            if (schema == null || partial) {

                return new UnknownWrapper<>(schema == null ? DataSchema.emptySchema() : schema,
                        NamedData.class);
            } else {

                return new Known<>(JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                        .create(),
                        JsonDataWrapper.DATA_TYPE,
                        "ToWrapper, schema=" + schema);
            }
        }
    }

    public static class CopySettings<D extends DidoData> {

        private final DataFactoryProvider<D> dataFactoryProvider;

        private DataSchema schema;

        private boolean partial;

        public CopySettings(DataFactoryProvider<D> dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public CopySettings<D> setSchema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public CopySettings<D> setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public Function<String, D> make() {

            if (schema == null || partial) {
                DataSchema partialSchema = schema == null ? DataSchema.emptySchema() : schema;

                return new Known<>(
                        JsonDataPartialCopy.registerPartialSchema(
                                        new GsonBuilder(), partialSchema, dataFactoryProvider)
                                .create(),
                        dataFactoryProvider.getDataType(),
                        "toCopy, partialSchema=" + partialSchema);
            } else {

                return new Known<>(JsonDataCopy.registerSchema(new GsonBuilder(), schema,
                                dataFactoryProvider)
                        .create(),
                        dataFactoryProvider.getDataType(),
                        "ToCopy, schema=" + schema);
            }
        }
    }

    static class UnknownWrapper<D extends DidoData> implements Function<String, D> {

        private final DataSchema partialSchema;

        private final Class<D> dataType;

        private volatile Known<D> known;

        UnknownWrapper(DataSchema partialSchema,
                       Class<D> dataType) {
            this.partialSchema = partialSchema;
            this.dataType = dataType;
        }

        @Override
        public D apply(String s) {
            if (known == null) {
                DataSchema schema = JsonSchemaExtractor
                        .registerPartialSchema(new GsonBuilder(), partialSchema)
                        .create()
                        .fromJson(s, DataSchema.class);
                Gson gson = JsonDataWrapper.registerSchema(new GsonBuilder(), schema).create();
                known = new Known<>(gson, dataType, toString());
            }
            return known.apply(s);
        }

        @Override
        public String toString() {
            return "ToWrapper, partialSchema=" + partialSchema;
        }
    }

    static class Known<D extends DidoData> implements Function<String, D> {

        private final Gson gson;

        private final Type dataType;

        private final String toString;

        Known(Gson gson, Type dataType, String toString) {
            this.gson = gson;
            this.dataType = dataType;
            this.toString = toString;
        }

        @Override
        public D apply(String s) {
            return gson.fromJson(s, dataType);
        }

        @Override
        public String toString() {
            return toString;
        }
    }

}
