package dido.json;

import dido.data.*;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

/**
 * Read a Stream of newline delimited JSON messages.
 *
 * @param <D> The type of Data that will be produced.
 */
public class StreamInJsonLines<D extends DidoData> implements DataInHow<InputStream, D> {

    private final Function<? super String, ? extends D> function;

    private StreamInJsonLines(Function<? super String, ? extends D> function) {

        this.function = function;
    }


    public static WrapperSettings asWrapper() {

        return new WrapperSettings();
    }

    public static  CopySettings<ArrayData> asCopy() {

        return asCopy(new ArrayDataDataFactoryProvider());
    }

    public static <D extends DidoData> CopySettings<D> asCopy(DataFactoryProvider<D> dataFactoryProvider) {

        return new CopySettings<>(dataFactoryProvider);
    }

    public static class WrapperSettings {

        private final JsonStringToData.WrapperSettings wrapperSettings = JsonStringToData.asWrapper();

        public WrapperSettings setSchema(DataSchema schema) {
            this.wrapperSettings.setSchema(schema);
            return this;
        }

        public WrapperSettings setPartial(boolean partial) {
            this.wrapperSettings.setPartial(partial);
            return this;
        }

        public DataInHow<InputStream, DidoData> make() {

            return new StreamInJsonLines<>(this.wrapperSettings.make());
        }
    }

    public static class CopySettings<D extends DidoData> {

        private final JsonStringToData.CopySettings<D> copySettings;

        CopySettings(DataFactoryProvider<D> dataFactoryProvider) {
            this.copySettings = JsonStringToData.asCopy(dataFactoryProvider);
        }

        public CopySettings<D> setSchema(DataSchema schema) {
            this.copySettings.setSchema(schema);
            return this;
        }

        public CopySettings<D> setPartial(boolean partial) {
            this.copySettings.setPartial(partial);
            return this;
        }

        public DataInHow<InputStream, D> make() {

            return new StreamInJsonLines<>(this.copySettings.make());
        }
    }

    @Override
    public Class<InputStream> getInType() {
        return InputStream.class;
    }

    @Override
    public DataIn<D> inFrom(InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return new DataIn<>() {

            @Override
            public D get() {
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
