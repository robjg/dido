package dido.data;

import dido.data.generic.GenericDataBuilder;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base class for {@link GenericDataBuilder}s that convert primitives into Objects.
 *
 * @param <B> The builder type.
 */
abstract public class DataBuilders<B extends DataBuilders<B>> {

    private DataBuilders() {}

    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    public abstract DidoData build();

    abstract public static class Indexed<B extends Indexed<B>> extends DataBuilders<B>
            implements IndexedDataBuilder<String> {

        @Override
        abstract public B setAt(int index, Object value);

        @Override
        public B setBooleanAt(int index, boolean value) {
            return setAt(index, value);
        }

        @Override
        public B setByteAt(int index, byte value) {
            return setAt(index, value);
        }

        @Override
        public B setCharAt(int index, char value) {
            return setAt(index, value);
        }

        @Override
        public B setShortAt(int index, short value) {
            return setAt(index, value);
        }

        @Override
        public B setIntAt(int index, int value) {
            return setAt(index, value);
        }

        @Override
        public B setLongAt(int index, long value) {
            return setAt(index, value);
        }

        @Override
        public B setFloatAt(int index, float value) {
            return setAt(index, value);
        }

        @Override
        public B setDoubleAt(int index, double value) {
            return setAt(index, value);
        }

        @Override
        public B setStringAt(int index, String value) {
            return setAt(index, value);
        }

        public B copy(IndexedData from) {

            DataSchema schema = from.getSchema();
            for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
                setAt(index, from.getAt(index));
            }
            return self();
        }

        public abstract DidoData build();
    }

    abstract public static class Fields<B extends Fields<B>> extends DataBuilders<B>
            implements DataBuilder {

        @Override
        abstract public B set(String field, Object value);

        @Override
        public B setBoolean(String field, boolean value) {
            return set(field, value);
        }

        @Override
        public B setByte(String field, byte value) {
            return set(field, value);
        }

        @Override
        public B setChar(String field, char value) {
            return set(field, value);
        }

        @Override
        public B setShort(String field, short value) {
            return set(field, value);
        }

        @Override
        public B setInt(String field, int value) {
            return set(field, value);
        }

        @Override
        public B setLong(String field, long value) {
            return set(field, value);
        }

        @Override
        public B setFloat(String field, float value) {
            return set(field, value);
        }

        @Override
        public B setDouble(String field, double value) {
            return set(field, value);
        }

        @Override
        public B setString(String field, String value) {
            return set(field, value);
        }

        public B copy(IndexedData from) {

            DataSchema schema = from.getSchema();
            for (String field : schema.getFieldNames()) {
                set(field, from.getAt(schema.getIndexNamed(field)));
            }
            return self();
        }
    }

    public B to(Consumer<? super DidoData> consumer) {
        consumer.accept(this.build());
        return self();
    }


    abstract public static class KnownSchema<B extends KnownSchema<B>>
        extends Indexed<B> implements DataBuilder {

        private final DataSchema schema;

        protected KnownSchema(DataSchema schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        protected DataSchema getSchema() {
            return schema;
        }

        public Values values() {
            return new Values(self(), schema);
        }

        public ValuesTo valuesTo(Consumer<? super DidoData> consumer) {
            return new ValuesTo(consumer, self());
        }

        @Override
        abstract public B set(String field, Object value);

        @Override
        public B setBoolean(String field, boolean value) {
            return set(field, value);
        }

        @Override
        public B setByte(String field, byte value) {
            return set(field, value);
        }

        @Override
        public B setChar(String field, char value) {
            return set(field, value);
        }

        @Override
        public B setShort(String field, short value) {
            return set(field, value);
        }

        @Override
        public B setInt(String field, int value) {
            return set(field, value);
        }

        @Override
        public B setLong(String field, long value) {
            return set(field, value);
        }

        @Override
        public B setFloat(String field, float value) {
            return set(field, value);
        }

        @Override
        public B setDouble(String field, double value) {
            return set(field, value);
        }

        @Override
        public B setString(String field, String value) {
            return set(field, value);
        }
    }

    /**
     * For A fluent way of providing Index Data for a known schema
     */
    public static class Values {

        private final Indexed<?> owner;

        private final DataSchema schema;

        Values(Indexed<?> owner, DataSchema schema) {
            this.owner = owner;
            this.schema = schema;
        }

        public DidoData of(Object... values) {
            int index = schema.firstIndex();
            for (int i = 0; i < values.length && index > 0; ++i) {
                owner.setAt(index, values[i]);
                index = schema.nextIndex(index);
            }
            return owner.build();
        }
    }

    public static class ValuesTo {

        private final Consumer<? super DidoData> consumer;
        private final KnownSchema<?> owner;

        ValuesTo(Consumer<? super DidoData> consumer, KnownSchema<?> owner) {
            this.consumer = consumer;
            this.owner = owner;
        }

        public ValuesTo of(Object... values) {
            DidoData data = owner.values().of(values);
            consumer.accept(data);
            return this;
        }
    }
}
