package dido.oddjob.stream;

import dido.data.*;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.DataOutHow;

import java.io.*;

/**
 * Streams a lines of text in and out, creating a {@link DidoData} record with the
 * single filed 'Line'.
 */
public class StreamLines {

    private static final String LINE = "Line";

    private static final DataSchema schema = SchemaBuilder.newInstance()
            .addNamed(LINE, String.class)
            .build();

    public static class In implements DataInHow<InputStream, NamedData> {

        DataBuilder<MapData> dataBuilder = MapData.builderForSchema(schema);

        @Override
        public Class<InputStream> getInType() {
            return InputStream.class;
        }

        @Override
        public DataIn<NamedData> inFrom(InputStream inputStream) {

            LineNumberReader reader = new LineNumberReader(
                    new InputStreamReader(inputStream));

            return new DataIn<>() {
                @Override
                public void close() {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }

                @Override
                public NamedData get() {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            return null;
                        } else {
                            return dataBuilder.with(LINE, line).build();
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
        }
    }

    public static class Out implements DataOutHow<OutputStream> {

        @Override
        public Class<OutputStream> getOutType() {
            return OutputStream.class;
        }

        @Override
        public DataOut outTo(OutputStream outputStream) {

            return new DataOut() {

                final PrintStream out = new PrintStream(outputStream);

                @Override
                public void close() {
                    out.close();
                }

                @Override
                public void accept(DidoData data) {
                    out.println(data.getStringNamed(LINE));
                }
            };
        }
    }
}
