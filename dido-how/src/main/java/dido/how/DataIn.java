package dido.how;


import dido.data.GenericData;

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
public interface DataIn<F> extends CloseableSupplier<GenericData<F>> {

    static <T> DataIn<T> empty() {
        return new DataIn<T>() {
            @Override
            public GenericData<T> get() {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }
}
