package dido.json;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.schema.HasSchema;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.how.util.OneAheadIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Read a Stream of newline delimited JSON messages. Requires a Function to convert each
 * line to Dido.
 *
 * @see JsonStringToData
 */
public class DataInJsonLines implements DataInHow<Reader> {

    private final Function<String, DidoData> function;

    private DataInJsonLines(Function<String, DidoData> function) {

        this.function = function;
    }

    public static DataInHow<Reader> withFunction(Function<String, DidoData> function) {
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

        Iterator<DidoData> originalIterator = reader.lines()
                .map(function)
                .iterator();

        Iterator<DidoData> iterator;
        DataSchema schema;

        if (function instanceof HasSchema hasSchema) {

            iterator = originalIterator;
            schema = hasSchema.getSchema();
        }
        else {
            if (originalIterator.hasNext()) {

                DidoData didoData = originalIterator.next();
                iterator = new OneAheadIterator<>(originalIterator, didoData);
                schema = didoData.getSchema();
            }
            else {
                return DataIn.empty();
            }
        }

        return new DataIn() {

            @Override
            public DataSchema getSchema() {
                return schema;
            }

            @Override
            public Iterator<DidoData> iterator() {

                return iterator;
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
