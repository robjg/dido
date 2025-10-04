package dido.data.util;

import dido.data.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
public class FieldValuesIn implements FromValues {

    private final DataFactory dataFactory;

    private final FieldSetter[] setters;

    private FieldValuesIn(DataFactory dataFactory) {
        this.dataFactory = dataFactory;
        WriteSchema schema = dataFactory.getSchema();
        setters = new FieldSetter[schema.lastIndex()];
        int i = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            setters[i++] = schema.getFieldSetterAt(index);
        }
    }

    public static FieldValuesIn withDataFactory(DataFactory dataFactory) {
        return new FieldValuesIn(dataFactory);
    }

    @Override
    public DataSchema getSchema() {
        return dataFactory.getSchema();
    }

    @Override
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

    @Override
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

    @Override
    public DidoData ofMap(Map<String, ?> map) {
        WritableData writableData = dataFactory.getWritableData();
        for (SchemaField schemaField : getSchema().getSchemaFields()) {
            Object value = map.get(schemaField.getName());
            if (value == null) {
                setters[schemaField.getIndex() - 1].clear(writableData);
            }
            else {
                setters[schemaField.getIndex() - 1].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

    @Override
    public DidoData copy(DidoData from) {
        return ofCollection(FieldValuesOut.collectionOf(from));
    }

    @Override
    public DataBuilder asBuilder() {
        return DataBuilder.forFactory(dataFactory);
    }

    /**
     * Allows a stream of values to be collected into an item of data. Not sure how useful this is.
     *
     * @return A collector.
     */
    @Override
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

    @Override
    public FromValues.Many many() {
        return new Many();
    }

    class Many implements FromValues.Many {

        private final Stream.Builder<DidoData> stream = Stream.builder();

        @Override
        public FromValues.Many of(Object... values) {
            stream.accept(FieldValuesIn.this.of(values));
            return this;
        }

        @Override
        public Stream<DidoData> toStream() {
            return stream.build();
        }

        @Override
        public List<DidoData> toList() {

            return toStream().collect(Collectors.toList());
        }
    }

}
