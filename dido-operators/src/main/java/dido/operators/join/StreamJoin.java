package dido.operators.join;

import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Something that can join two streams of {@link dido.data.DidoData} (not Java {@link java.util.stream.Stream}s).
 *
 */
public interface StreamJoin {

    Consumer<DidoData> getPrimary();

    Consumer<DidoData> getSecondary();

    void setTo(Consumer<? super DidoData> to);
}
