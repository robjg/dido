package dido.operators;

import dido.data.GenericData;
import dido.data.IndexedData;

import java.util.function.Consumer;

/**
 * Something that can join two streams of {@link dido.data.IndexedData} (not Java {@link java.util.stream.Stream}s).
 *
 *
 * @param <F> The field type.
 */
public interface StreamJoin<F> {

    Consumer<IndexedData<F>> getPrimary();

    Consumer<IndexedData<F>> getSecondary();

    void setTo(Consumer<? super GenericData<F>> to);
}
