package dido.data.util;

import dido.data.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A fluent way of creating {@link DidoData} from field values for a known schema.
 */
public class FieldValuesIn {

    private final DataFactory dataFactory;

    private final FieldSetter[] setters;

    private FieldValuesIn(DataFactory dataFactory) {
        this.dataFactory = dataFactory;
        DataSchema schema = dataFactory.getSchema();
        WriteStrategy writeStrategy = WriteStrategy.fromSchema(schema);
        setters = new FieldSetter[schema.lastIndex()];
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            setters[index - 1] = writeStrategy.getFieldSetterAt(index);
        }
    }

    public static FieldValuesIn withDataFactory(DataFactory dataFactory) {
        return new FieldValuesIn(dataFactory);
    }

    public DataSchema getSchema() {
        return dataFactory.getSchema();
    }

    public DidoData of(Object... values) {
        WritableData writableData = dataFactory.getWritableData();
        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];
            if (value != null) {
                setters[i].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

    public DidoData ofCollection(Collection<?> values) {
        WritableData writableData = dataFactory.getWritableData();
        int i = 0;
        for (Object value : values) {
            if (value == null) {
                setters[i++].clear(writableData);
            }
            else {
                setters[i++].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

    public DidoData copy(DidoData from) {
        return ofCollection(FieldValuesOut.collectionOf(from));
    }

    /**
     * Allows a stream of values to be collected into an item of data. Not sure how useful this is.
     *
     * @return A collector.
     */
    public Collector<Object, WritableData, DidoData> toCollector() {
        return new Collector<>() {
            int i = 1;

            @Override
            public Supplier<WritableData> supplier() {
                return dataFactory::getWritableData;
            }

            @Override
            public BiConsumer<WritableData, Object> accumulator() {
                return (writableData, o) -> writableData.setAt(i++, o);
            }

            @Override
            public BinaryOperator<WritableData> combiner() {
                return (left, right) -> {
                    throw new UnsupportedOperationException("Can't run in parallel");
                };
            }

            @Override
            public Function<WritableData, DidoData> finisher() {
                return writableData -> dataFactory.toData();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
    }

    public Many many() {
        return new Many();
    }

    public class Many {

        private final Stream.Builder<DidoData> stream = Stream.builder();

        public Many of(Object... values) {
            stream.accept(FieldValuesIn.this.of(values));
            return this;
        }

        public Stream<DidoData> toStream() {
            return stream.build();
        }

        public List<DidoData> toList() {

            return toStream().collect(Collectors.toList());
        }
    }

}
