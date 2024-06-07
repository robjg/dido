package dido.how;


import dido.data.DidoData;

import java.util.Iterator;

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
public interface DataIn extends CloseableSupplier<DidoData>, Iterable<DidoData> {

    static DataIn empty() {
        return new DataIn() {
            @Override
            public DidoData get() {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    default Iterator<DidoData> iterator() {

        return new Iterator<>() {

            DidoData next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = get();
                }
                return next != null;
            }

            @Override
            public DidoData next() {
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
        };
    }
}
