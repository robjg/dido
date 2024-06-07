package dido.how;


import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Definition of a something that pushes Generic Data and can be subscribed to.
 *
 */
public interface Subscribable {

    AutoCloseable subscribe(Consumer<? super DidoData> subscriber);
}
