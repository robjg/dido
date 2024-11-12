package dido.json;

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
 * Read a Stream of newline delimited JSON messages. Requires a Function to convert each
 * line to Dido.
 *
 * @see JsonStringToData
 */
public class DataInJsonLines implements DataInHow<Reader> {

    private final Function<? super String, ? extends DidoData> function;

    private DataInJsonLines(Function<? super String, ? extends DidoData> function) {

        this.function = function;
    }

    public static DataInHow<Reader> withFunction(Function<? super String, ? extends DidoData> function) {
        return new DataInJsonLines(function);
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
