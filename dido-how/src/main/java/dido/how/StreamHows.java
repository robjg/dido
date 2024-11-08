package dido.how;

import java.io.*;

/**
 * Utility methods for going to and from Input/Output streams.
 */
public class StreamHows {

    public static DataOutHow<OutputStream> fromWriterHow(DataOutHow<? super Writer> writerHow) {

        return new DataOutHow<>() {
            @Override
            public Class<OutputStream> getOutType() {
                return OutputStream.class;
            }

            @Override
            public DataOut outTo(OutputStream outTo) {
                return writerHow.outTo(new OutputStreamWriter(outTo));
            }

            @Override
            public String toString() {
                return writerHow.toString();
            }
        };
    }

    public static DataInHow<InputStream> fromReaderHow(DataInHow<? super Reader> readerHow) {

        return new DataInHow<>() {
            @Override
            public Class<InputStream> getInType() {
                return InputStream.class;
            }

            @Override
            public DataIn inFrom(InputStream dataIn) {
                return readerHow.inFrom(new InputStreamReader(dataIn));
            }

            @Override
            public String toString() {
                return readerHow.toString();
            }
        };
    }
}
