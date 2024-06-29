package dido.data.generic;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.IndexedDataBuilder;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base class for {@link GenericDataBuilder}s that convert primitives into Objects.
 *
 * @param <F> The field type.
 * @param <B> The builder type.
 */
abstract public class GenericDataBuilders<F, D extends GenericData<F>, B extends GenericDataBuilders<F, D, B>> {

    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    public abstract D build();

    abstract public static class Indexed<F, D extends GenericData<F>, B extends Indexed<F, D, B>>
            extends GenericDataBuilders<F, D, B>
            implements IndexedDataBuilder<D, B> {

        @Override
        abstract public B withAt(int index, Object value);

        @Override
        public B withBooleanAt(int index, boolean value) {
            return withAt(index, value);
        }

        @Override
        public B withByteAt(int index, byte value) {
            return withAt(index, value);
        }

        @Override
        public B withCharAt(int index, char value) {
            return withAt(index, value);
        }

        @Override
        public B withShortAt(int index, short value) {
            return withAt(index, value);
        }

        @Override
        public B withIntAt(int index, int value) {
            return withAt(index, value);
        }

        @Override
        public B withLongAt(int index, long value) {
            return withAt(index, value);
        }

        @Override
        public B withFloatAt(int index, float value) {
            return withAt(index, value);
        }

        @Override
        public B withDoubleAt(int index, double value) {
            return withAt(index, value);
        }

        @Override
        public B withStringAt(int index, String value) {
            return withAt(index, value);
        }

        public B copy(DidoData from) {

            DataSchema schema = from.getSchema();
            for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
                withAt(index, from.getAt(index));
            }
            return self();
        }

    }

    abstract public static class Fields<F, B extends Fields<F, B>>
            extends GenericDataBuilders<F, GenericData<F>, B>
            implements GenericDataBuilder<F> {

        @Override
        abstract public B with(F field, Object value);

        @Override
        public B withBoolean(F field, boolean value) {
            return with(field, value);
        }

        @Override
        public B withByte(F field, byte value) {
            return with(field, value);
        }

        @Override
        public B withChar(F field, char value) {
            return with(field, value);
        }

        @Override
        public B withShort(F field, short value) {
            return with(field, value);
        }

        @Override
        public B withInt(F field, int value) {
            return with(field, value);
        }

        @Override
        public B withLong(F field, long value) {
            return with(field, value);
        }

        @Override
        public B withFloat(F field, float value) {
            return with(field, value);
        }

        @Override
        public B withDouble(F field, double value) {
            return with(field, value);
        }

        @Override
        public B withString(F field, String value) {
            return with(field, value);
        }

        public B copy(GenericData<F> from) {

            GenericDataSchema<F> schema = from.getSchema();
            for (F field : schema.getFields()) {
                with(field, from.getAt(schema.getIndexOf(field)));
            }
            return self();
        }
    }

    public B to(Consumer<? super GenericData<F>> consumer) {
        consumer.accept(this.build());
        return self();
    }


    abstract public static class KnownSchema<F, D extends GenericData<F>, B extends KnownSchema<F, D, B>>
        extends Indexed<F, D, B> implements GenericDataBuilder<F> {

        private final GenericDataSchema<F> schema;

        protected KnownSchema(GenericDataSchema<F> schema) {
            super();
            this.schema = Objects.requireNonNull(schema);
        }

        protected GenericDataSchema<F> getSchema() {
            return schema;
        }

        public Values<F> values() {
            return new Values<>(self(), schema);
        }

        public ValuesTo<F> valuesTo(Consumer<? super GenericData<F>> consumer) {
            return new ValuesTo<>(consumer, self());
        }

        @Override
        abstract public B with(F field, Object value);

        @Override
        public B withBoolean(F field, boolean value) {
            return with(field, value);
        }

        @Override
        public B withByte(F field, byte value) {
            return with(field, value);
        }

        @Override
        public B withChar(F field, char value) {
            return with(field, value);
        }

        @Override
        public B withShort(F field, short value) {
            return with(field, value);
        }

        @Override
        public B withInt(F field, int value) {
            return with(field, value);
        }

        @Override
        public B withLong(F field, long value) {
            return with(field, value);
        }

        @Override
        public B withFloat(F field, float value) {
            return with(field, value);
        }

        @Override
        public B withDouble(F field, double value) {
            return with(field, value);
        }

        @Override
        public B withString(F field, String value) {
            return with(field, value);
        }
    }

    /**
     * For A fluent way of providing Index Data for a known schema
     *
     * @param <F> The field type.
     */
    public static class Values<F> {

        private final Indexed<F, ?, ?> owner;

        private final GenericDataSchema<F> schema;

        Values(Indexed<F, ?, ?> owner, GenericDataSchema<F> schema) {
            this.owner = owner;
            this.schema = schema;
        }

        public GenericData<F> of(Object... values) {
            int index = schema.firstIndex();
            for (int i = 0; i < values.length && index > 0; ++i) {
                owner.withAt(index, values[i]);
                index = schema.nextIndex(index);
            }
            return owner.build();
        }
    }

    public static class ValuesTo<F> {

        private final Consumer<? super GenericData<F>> consumer;
        private final KnownSchema<F, ?, ?> owner;

        ValuesTo(Consumer<? super GenericData<F>> consumer, KnownSchema<F, ?, ?> owner) {
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
