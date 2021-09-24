package dido.pickles;

import java.util.function.Supplier;

/**
 * A Supplier that can be closed.
 *
 * @param <T> The type of the Supplier.
 */
public interface CloseableSupplier<T> extends Supplier<T>, AutoCloseable {

}
