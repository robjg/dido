package dido.how;

import dido.data.DidoData;

import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * Something that data can be written to. This is one of the fundamental
 * concepts in Dido.
 * 
 * @see DataOutHow
 * @see DataIn
 * 
 * @author rob
 *
 */
public interface DataOut extends CloseableConsumer<DidoData> {

    default Collector<DidoData, DataOut, Void> asCollector() {

        return new Collector<>() {
            @Override
            public Supplier<DataOut> supplier() {
                return () -> DataOut.this;
            }

            @Override
            public BiConsumer<DataOut, DidoData> accumulator() {
                return Consumer::accept;
            }

            @Override
            public BinaryOperator<DataOut> combiner() {
                return (dataOut, dataOut2) -> dataOut;
            }

            @Override
            public Function<DataOut, Void> finisher() {
                return dataOut -> {
                    try {
                        dataOut.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
    }

    @Override
    void close();
}
