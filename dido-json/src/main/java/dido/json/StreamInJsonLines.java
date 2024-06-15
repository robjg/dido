package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

public class StreamInJsonLines implements DataInHow<InputStream> {

    private final Function<? super String, ? extends DidoData> function;

    private StreamInJsonLines(Function<? super String, ? extends DidoData> function) {

        this.function = function;
    }

    public static DataInHow<InputStream> asWrapperWithSchema(DataSchema schema) {

        return new Settings()
                .setSchema(schema)
                .make();
    }

    public static DataInHow<InputStream> asWrapperWithPartialSchema(DataSchema partialSchema) {

        return new Settings()
                .setSchema(partialSchema)
                .setPartial(true)
                .make();
    }

    public static DataInHow<InputStream> asCopyWithSchema(DataSchema schema) {

        return new Settings()
                .setCopy(true)
                .setSchema(schema)
                .make();
    }

    public static DataInHow<InputStream> asCopyWithPartialSchema(DataSchema partialSchema) {

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

        public Settings setSchema(DataSchema schema) {
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

        public DataInHow<InputStream> make() {

            return new StreamInJsonLines(this.settings.make());
        }
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn inFrom(InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return new DataIn() {

            @Override
            public DidoData get() {
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
