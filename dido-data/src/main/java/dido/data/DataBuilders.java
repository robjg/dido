package dido.data;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base class for {@link GenericDataBuilder}s that convert primitives into Objects.
 *
 * @param <F> The field type.
 * @param <B> The builder type.
 */
abstract public class DataBuilders<F, B extends DataBuilders<F, B>> {

    private DataBuilders() {}
    abstract protected B self();

    public abstract GenericData<F> build();
    abstract public static class Indexed<F, B extends Indexed<F, B>> extends DataBuilders<F, B>
            implements IndexedDataBuilder<F> {

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

        public B copy(IndexedData<F> from) {

            DataSchema<F> schema = from.getSchema();
            for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
                setAt(index, from.getAt(index));
            }
            return self();
        }
    }

    abstract public static class Fields<F, B extends Fields<F, B>> extends DataBuilders<F, B>
            implements GenericDataBuilder<F> {

        @Override
        abstract public B set(F field, Object value);

        @Override
        public B setBoolean(F field, boolean value) {
            return set(field, value);
        }

        @Override
        public B setByte(F field, byte value) {
            return set(field, value);
        }

        @Override
        public B setChar(F field, char value) {
            return set(field, value);
        }

        @Override
        public B setShort(F field, short value) {
            return set(field, value);
        }

        @Override
        public B setInt(F field, int value) {
            return set(field, value);
        }

        @Override
        public B setLong(F field, long value) {
            return set(field, value);
        }

        @Override
        public B setFloat(F field, float value) {
            return set(field, value);
        }

        @Override
        public B setDouble(F field, double value) {
            return set(field, value);
        }

        @Override
        public B setString(F field, String value) {
            return set(field, value);
        }

        public B copy(IndexedData<F> from) {

            DataSchema<F> schema = from.getSchema();
            for (F field : schema.getFields()) {
                set(field, from.getAt(schema.getIndex(field)));
            }
            return self();
        }
    }

    public B to(Consumer<? super GenericData<F>> consumer) {
        consumer.accept(this.build());
        return self();
    }


    abstract public static class KnownSchema<F, B extends KnownSchema<F, B>>
        extends Indexed<F, B> implements GenericDataBuilder<F> {

        private final DataSchema<F> schema;

        protected KnownSchema(DataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        protected DataSchema<F> getSchema() {
            return schema;
        }

        public Values<F> values() {
            return new Values<>(self(), schema);
        }

        public ValuesTo<F> valuesTo(Consumer<? super GenericData<F>> consumer) {
            return new ValuesTo<>(consumer, self());
        }

        @Override
        abstract public B set(F field, Object value);

        @Override
        public B setBoolean(F field, boolean value) {
            return set(field, value);
        }

        @Override
        public B setByte(F field, byte value) {
            return set(field, value);
        }

        @Override
        public B setChar(F field, char value) {
            return set(field, value);
        }

        @Override
        public B setShort(F field, short value) {
            return set(field, value);
        }

        @Override
        public B setInt(F field, int value) {
            return set(field, value);
        }

        @Override
        public B setLong(F field, long value) {
            return set(field, value);
        }

        @Override
        public B setFloat(F field, float value) {
            return set(field, value);
        }

        @Override
        public B setDouble(F field, double value) {
            return set(field, value);
        }

        @Override
        public B setString(F field, String value) {
            return set(field, value);
        }
    }

    /**
     * For A fluent way of providing Index Data for a known schema
     *
     * @param <F> The field type.
     */
    public static class Values<F> {

        private final IndexedDataBuilder<F> owner;

        private final DataSchema<F> schema;

        Values(IndexedDataBuilder<F> owner, DataSchema<F> schema) {
            this.owner = owner;
            this.schema = schema;
        }

        public GenericData<F> of(Object... values) {
            int index = schema.firstIndex();
            for (int i = 0; i < values.length && index > 0; ++i) {
                owner.setAt(index, values[i]);
                index = schema.nextIndex(index);
            }
            return owner.build();
        }
    }

    public static class ValuesTo<F> {

        private final Consumer<? super GenericData<F>> consumer;
        private final KnownSchema<F, ?> owner;

        ValuesTo(Consumer<? super GenericData<F>> consumer, KnownSchema<F, ?> owner) {
            this.consumer = consumer;
            this.owner = owner;
        }

        public ValuesTo<F> of(Object... values) {
            GenericData<F> data = owner.values().of(values);
            consumer.accept(data);
            return this;
        }
    }
}
