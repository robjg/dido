package dido.how;


import dido.data.DidoData;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Something that provides data. Implementation will typically wrap some underlying input of data
 * such as JSON from a file or Results from a SQL query.
 * <p>
 * After use, {@link #close()} should be called
 * which will close underlying resources. Calling {@code close()} on the {@link Stream} provided from
 * {@link #stream()} will not close resources. This follows the same pattern that calling {@code close()}
 * on the {@code Stream} provided by {@link BufferedReader#lines()} does not close the reader.
 *
 *
 * @see DataInHow
 * @see DataOut
 *
 * @author rob
 */
public interface DataIn extends Iterable<DidoData>, AutoCloseable {

    static DataIn of(DidoData... data) {
        return new DataIn() {
            int index = 0;

            @Override
            public Iterator<DidoData> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return index < data.length - 1;
                    }

                    @Override
                    public DidoData next() {
                        return data[index++];
                    }
                };
            }
        };
    }

    default Stream<DidoData> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    @Override
    default void close() {
    }
}
