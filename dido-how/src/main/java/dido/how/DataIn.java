package dido.how;


import dido.data.GenericData;

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
public interface DataIn<F> extends CloseableSupplier<GenericData<F>>, Iterable<GenericData<F>> {

    static <T> DataIn<T> empty() {
        return new DataIn<>() {
            @Override
            public GenericData<T> get() {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    default Iterator<GenericData<F>> iterator() {

        return new Iterator<>() {

            GenericData<F> next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    next = get();
                }
                return next != null;
            }

            @Override
            public GenericData<F> next() {
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
        };
    }
}
