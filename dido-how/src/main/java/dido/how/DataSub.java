package dido.how;


import dido.data.GenericData;

import java.util.function.Consumer;

/**
 * Definition of a Generic Data Subscriber
 *
 * @param <F> The Field Type of the Data.
 */
public interface DataSub<F> {

    AutoCloseable subscribe(Consumer<GenericData<F>> to);
}
