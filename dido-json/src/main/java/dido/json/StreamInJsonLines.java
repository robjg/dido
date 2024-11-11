package dido.json;

import dido.data.ArrayDataDataFactoryProvider;
import dido.data.DataFactoryProvider;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Read a Stream of newline delimited JSON messages.
 *
 */
public class StreamInJsonLines implements DataInHow<Reader> {

    private final Function<? super String, ? extends DidoData> function;

    private StreamInJsonLines(Function<? super String, ? extends DidoData> function) {

        this.function = function;
    }

    public static WrapperSettings asWrapper() {

        return new WrapperSettings();
    }

    public static  CopySettings asCopy() {

        return asCopy(new ArrayDataDataFactoryProvider());
    }

    public static CopySettings asCopy(DataFactoryProvider dataFactoryProvider) {

        return new CopySettings(dataFactoryProvider);
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

        public DataInHow<Reader> make() {

            return new StreamInJsonLines(this.wrapperSettings.make());
        }
    }

    public static class CopySettings {

        private final JsonStringToData.CopySettings copySettings;

        CopySettings(DataFactoryProvider dataFactoryProvider) {
            this.copySettings = JsonStringToData.asCopy(dataFactoryProvider);
        }

        public CopySettings setSchema(DataSchema schema) {
            this.copySettings.setSchema(schema);
            return this;
        }

        public CopySettings setPartial(boolean partial) {
            this.copySettings.setPartial(partial);
            return this;
        }

        public DataInHow<Reader> make() {

            return new StreamInJsonLines(this.copySettings.make());
        }
    }

    @Override
    public Class<Reader> getInType() {
        return Reader.class;
    }

    @Override
    public DataIn inFrom(Reader dataIn) {

        BufferedReader reader;
        if (dataIn instanceof BufferedReader) {
            reader = ((BufferedReader) dataIn);
        }
        else {
            reader = new BufferedReader(dataIn);
        }

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
