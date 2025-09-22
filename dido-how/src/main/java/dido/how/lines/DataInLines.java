package dido.how.lines;

import dido.data.DidoData;
import dido.data.immutable.SingleData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Reads In Lines of text, creating a {@link DidoData} record with a
 * single field that defaults to 'Line'.
 */
public class DataInLines implements DataInHow<Reader> {

    private static final String LINE = "Line";

    private final SingleData.ObjectType<String> singleDataType;

    private DataInLines(Settings settings) {

        singleDataType = SingleData.named(
                Objects.requireNonNullElse(settings.fieldName, LINE)).type(String.class);
    }

    public static class Settings {

        private String fieldName;

        public Settings fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public DataIn fromReader(Reader reader) {
            return make().inFrom(reader);
        }

        public DataIn fromPath(Path path) {
            try {
                return make().inFrom(Files.newBufferedReader(path));
            } catch (IOException e) {
                throw DataException.of(e);
            }
        }

        public DataIn fromInputStream(InputStream inputStream) {

            return make().inFrom(new BufferedReader(
                    new InputStreamReader(inputStream)));
        }

        public DataInLines make() {
            return new DataInLines(this);
        }
    }

    public static DataIn fromReader(Reader reader) {
        return with().fromReader(reader);
    }

    public static DataIn fromPath(Path path) {
        return with().fromPath(path);
    }

    public static DataIn fromInputStream(InputStream inputStream) {

        return with().fromInputStream(inputStream);
    }

    public static Settings with() {
        return new Settings();
    }

    public static DataInLines withDefaults() {
        return with().make();
    }


    @Override
    public Class<Reader> getInType() {
        return Reader.class;
    }

    @Override
    public DataIn inFrom(Reader reader) {

        if (reader instanceof BufferedReader) {
            return new In((BufferedReader) reader);
        }
        else {
            return new In(new BufferedReader(reader));
        }
    }

    class In implements DataIn {

        private final BufferedReader reader;

        In(BufferedReader bufferedReader) {
            this.reader = bufferedReader;
        }

        @Override
        public void close() {
            try {
                reader.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Iterator<DidoData> iterator() {
            return stream().iterator();
        }

        @Override
        public Stream<DidoData> stream() {
            return reader.lines().map(singleDataType::of);
        }
    }
}