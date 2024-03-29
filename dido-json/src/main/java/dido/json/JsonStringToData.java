package dido.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dido.data.DataSchema;
import dido.data.GenericData;

import java.util.function.Function;

public class JsonStringToData {

    public static Function<String, GenericData<String>> asWrapperWithSchema(DataSchema<String> schema) {

        return new Known(JsonDataWrapper.registerSchema(new GsonBuilder(), schema)
                .create(),
                "ToWrapper, schema=" + schema);
    }

    public static Function<String, GenericData<String>> asWrapperWithPartialSchema(DataSchema<String> partialSchema) {

        return new UnknownWrapper(partialSchema == null ? DataSchema.emptySchema() : partialSchema);
    }

    public static Function<String, GenericData<String>> asCopyWithSchema(DataSchema<String> schema) {

        return new Known(JsonDataCopy.registerSchema(new GsonBuilder(), schema)
                .create(),
                "ToCopy, schema=" + schema);
    }

    public static Function<String, GenericData<String>> asCopyWithPartialSchema(DataSchema<String> partialSchema) {

        partialSchema = partialSchema == null ? DataSchema.emptySchema() : partialSchema;

        return new Known(JsonDataPartialCopy.registerPartialSchema(new GsonBuilder(), partialSchema)
                .create(),
                "toCopy, partialSchema=" + partialSchema);
    }

    public static Settings withSettings() {
        return new Settings();
    }

    public static class Settings {

        private DataSchema<String> schema;

        private boolean partial;

        private boolean copy;

        public Settings setSchema(DataSchema<String> schema) {
            this.schema = schema;
            return this;
        }

        public Settings setPartial(boolean partial) {
            this.partial = partial;
            return this;
        }

        public Settings setCopy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public Function<String, GenericData<String>> make() {

            if (copy) {
                if (schema == null || partial) {
                    return asCopyWithPartialSchema(schema);
                }
                else {
                    return asCopyWithSchema(schema);
                }
            }
            else {
                if (schema == null || partial) {
                    return asWrapperWithPartialSchema(schema);
                }
                else {
                    return asWrapperWithSchema(schema);
                }
            }
        }
    }

    static class UnknownWrapper implements Function<String, GenericData<String>> {

        private final DataSchema<String> partialSchema;

        private volatile Known known;

        UnknownWrapper(DataSchema<String> partialSchema) {
            this.partialSchema = partialSchema;
        }

        @Override
        public GenericData<String> apply(String s) {
            if (known == null) {
                DataSchema<String> schema = JsonSchemaExtractor
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

    static class Known implements Function<String, GenericData<String>> {

        private final Gson gson;

        private final String toString;
        Known(Gson gson, String toString) {
            this.gson = gson;
            this.toString = toString;
        }

        @Override
        public GenericData<String> apply(String s) {
            return gson.fromJson(s, GenericData.class);
        }

        @Override
        public String toString() {
            return toString;
        }
    }

}
