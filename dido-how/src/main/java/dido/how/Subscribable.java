package dido.how;


import dido.data.GenericData;

import java.util.function.Consumer;

/**
 * Definition of a something that pushes Generic Data and can be subscribed to.
 *
 * @param <F> The Field Type of the Data.
 */
public interface Subscribable<F> {

    AutoCloseable subscribe(Consumer<? super GenericData<F>> subscriber);
}
