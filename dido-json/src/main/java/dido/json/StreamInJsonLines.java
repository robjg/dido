package dido.json;

import com.google.gson.reflect.TypeToken;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.function.Function;

public class StreamInJsonLines implements DataInHow<String, InputStream> {

    private static final Type stringGenericDataType =
            new TypeToken<GenericData<String>>() {
            }.getType();

    private final Function<String, GenericData<String>> function;

    private StreamInJsonLines(Function<String, GenericData<String>> function) {

        this.function = function;
    }

    public static DataInHow<String, InputStream> asWrapperWithSchema(DataSchema<String> schema) {

        return new Settings()
                .setSchema(schema)
                .make();
    }

    public static DataInHow<String, InputStream> asWrapperWithPartialSchema(DataSchema<String> partialSchema) {

        return new Settings()
                .setSchema(partialSchema)
                .setPartial(true)
                .make();
    }

    public static DataInHow<String, InputStream> asCopyWithSchema(DataSchema<String> schema) {

        return new Settings()
                .setCopy(true)
                .setSchema(schema)
                .make();
    }

    public static DataInHow<String, InputStream> asCopyWithPartialSchema(DataSchema<String> partialSchema) {

        return new Settings()
                .setCopy(true)
                .setSchema(partialSchema)
                .setPartial(true)
                .make();
    }

    public static Settings settings() {

        return new Settings();
    }

    public static class Settings {

        private final JsonStringToData.Settings settings = JsonStringToData.withSettings();

        public Settings setSchema(DataSchema<String> schema) {
            this.settings.setSchema(schema);
            return this;
        }

        public Settings setPartial(boolean partial) {
            this.settings.setPartial(partial);
            return this;
        }

        public Settings setCopy(boolean copy) {
            this.settings.setCopy(copy);
            return this;
        }

        public DataInHow<String, InputStream> make() {

            return new StreamInJsonLines(this.settings.make());
        }
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<String> inFrom(InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return new DataIn<>() {

            @Override
            public GenericData<String> get() {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        return null;
                    } else {
                        return function.apply(line);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        };
    }

    @Override
    public String toString() {
        return "JsonLines " + function;
    }
}
