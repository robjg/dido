package dido.how;


import dido.data.DidoData;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Something that data provide data by being read. This is one of the 
 * fundamental concepts in Dido.
 * 
 * @see DataInHow
 * @see DataOut
 * 
 * @author rob
 *
 */
public interface DataIn<D extends DidoData> extends CloseableSupplier<D>, Iterable<D> {

    static <D extends DidoData> DataIn<D> empty() {
        return new DataIn<>() {
            @Override
            public D get() {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    default Iterator<D> iterator() {

        return new Iterator<>() {

            D next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = get();
                }
                return next != null;
            }

            @Override
            public D next() {
                try {
                    return next;
                } finally {
                    next = null;
                }
            }

        };
    }

    default Stream<D> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
}
