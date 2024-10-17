package dido.json;

import dido.data.*;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Read a Stream of newline delimited JSON messages.
 *
 */
public class StreamInJsonLines implements DataInHow<InputStream> {

    private final Function<? super String, ? extends DidoData> function;

    private StreamInJsonLines(Function<? super String, ? extends DidoData> function) {

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

        public DataInHow<InputStream> make() {

            return new StreamInJsonLines(this.wrapperSettings.make());
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

        public DataInHow<InputStream> make() {

            return new StreamInJsonLines(this.copySettings.make());
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
            public Iterator<DidoData> iterator() {

                return stream().iterator();
            }

            @Override
            public Stream<DidoData> stream() {
                return reader.lines()
                        .map(function);
            }

            @Override
            public void close() {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw DataException.of(e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "JsonLines " + function;
    }
}
