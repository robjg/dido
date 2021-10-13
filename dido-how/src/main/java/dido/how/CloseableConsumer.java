package dido.how;

import java.util.function.Consumer;

/**
 * A Consumer that can be closed.
 *
 * @param <T> The type of the consumer.
 */
public interface CloseableConsumer<T> extends Consumer<T>, AutoCloseable {

}
