package dido.oddjob.stream;

import dido.data.DidoData;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.how.lines.DataInLines;
import dido.how.lines.DataOutLines;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Streams a lines of text in and out, creating a {@link DidoData} record with the
 * single filed 'Line'.
 */
public class StreamLines {

    public static class In implements DataInHow<InputStream> {

        @Override
        public Class<InputStream> getInType() {
            return InputStream.class;
        }

        @Override
        public DataIn inFrom(InputStream inputStream) {

            return DataInLines.fromInputStream(inputStream);
        }
    }

    public static class Out implements DataOutHow<OutputStream> {

        @Override
        public Class<OutputStream> getOutType() {
            return OutputStream.class;
        }

        @Override
        public DataOut outTo(OutputStream outTo) {

            return DataOutLines.toOutputStream(outTo);
        }
    }
}
