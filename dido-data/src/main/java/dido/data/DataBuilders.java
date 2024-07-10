package dido.data;

import dido.data.generic.GenericDataBuilder;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base class for {@link GenericDataBuilder}s that convert primitives into Objects.
 *
 * @param <D> The data type.
 * @param <B> The builder type.
 */
abstract public class DataBuilders<D extends DidoData, B extends DataBuilders<D, B>> {

    private DataBuilders() {
    }

    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    public abstract D build();

    public B to(Consumer<? super DidoData> consumer) {
        consumer.accept(this.build());
        return self();
    }

    abstract public static class Indexed<D extends DidoData, B extends Indexed<D, B>> extends DataBuilders<D, B>
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

    /**
     *
     */
    abstract public static class NamedFields<B extends NamedFields<B>>
            extends DataBuilders<NamedData, B>
            implements NamedDataBuilder {

        @Override
        abstract public B with(String field, Object value);

        public B withBoolean(String field, boolean value) {
            return with(field, value);
        }

        public B withByte(String field, byte value) {
            return with(field, value);
        }

        @Override
        public B withChar(String field, char value) {
            return with(field, value);
        }

        @Override
        public B withShort(String field, short value) {
            return with(field, value);
        }

        @Override
        public B withInt(String field, int value) {
            return with(field, value);
        }

        @Override
        public B withLong(String field, long value) {
            return with(field, value);
        }

        @Override
        public B withFloat(String field, float value) {
            return with(field, value);
        }

        @Override
        public B withDouble(String field, double value) {
            return with(field, value);
        }

        @Override
        public B withString(String field, String value) {
            return with(field, value);
        }

        public B copy(DidoData from) {

            DataSchema schema = from.getSchema();
            for (String field : schema.getFieldNames()) {
                with(field, from.getAt(schema.getIndexNamed(field)));
            }
            return self();
        }

    }

    /**
     * @param <B>
     */
    abstract public static class NamedKnownSchema<B extends NamedKnownSchema<B>>
            extends NamedFields<B> {

        private final DataSchema schema;

        protected NamedKnownSchema(DataSchema schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        protected DataSchema getSchema() {
            return schema;
        }

        public NamedValues values() {
            return new NamedValues(this, schema);
        }

    }

    /**
     * Only use this if index building is faster than name building.
     *
     * @param <B>
     */
    abstract public static class KnownSchema<B extends KnownSchema<B, S>, S extends DataSchema>
            extends Indexed<NamedData, B> implements NamedDataBuilder {

        private final S schema;

        protected KnownSchema(S schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        protected S getSchema() {
            return schema;
        }

        public Values values() {
            return new Values(self(), getSchema());
        }

        public ValuesTo valuesTo(Consumer<? super DidoData> consumer) {
            return new ValuesTo(consumer, self());
        }

        @Override
        abstract public B with(String field, Object value);

        @Override
        public B withBoolean(String field, boolean value) {
            return with(field, value);
        }

        @Override
        public B withByte(String field, byte value) {
            return with(field, value);
        }

        @Override
        public B withChar(String field, char value) {
            return with(field, value);
        }

        @Override
        public B withShort(String field, short value) {
            return with(field, value);
        }

        @Override
        public B withInt(String field, int value) {
            return with(field, value);
        }

        @Override
        public B withLong(String field, long value) {
            return with(field, value);
        }

        @Override
        public B withFloat(String field, float value) {
            return with(field, value);
        }

        @Override
        public B withDouble(String field, double value) {
            return with(field, value);
        }

        @Override
        public B withString(String field, String value) {
            return with(field, value);
        }
    }

    /**
     * For A fluent way of providing Index Data for a known schema
     */
    public static class Values {

        private final Indexed<NamedData, ?> owner;

        private final DataSchema schema;

        Values(Indexed<NamedData, ?> owner, DataSchema schema) {
            this.owner = owner;
            this.schema = schema;
        }

        public NamedData of(Object... values) {
            int index = schema.firstIndex();
            for (int i = 0; i < values.length && index > 0; ++i) {
                owner.withAt(index, values[i]);
                index = schema.nextIndex(index);
            }
            return owner.build();
        }
    }

    /**
     * For A fluent way of providing Named Data for a known schema.
     */
    public static class NamedValues {

        private final NamedFields<?> owner;

        private final DataSchema schema;

        NamedValues(NamedFields<?> owner, DataSchema schema) {
            this.owner = Objects.requireNonNull(owner);
            this.schema = Objects.requireNonNull(schema);
        }

        public DidoData of(Object... values) {
            for (int i = 0; i < values.length; ++i) {
                String field = schema.getFieldNameAt(i + 1);
                if (field == null) {
                    throw new IllegalArgumentException("No field for index " + i + 1);
                }
                owner.with(field, values[i]);
            }
            return owner.build();
        }
    }

    public static class ValuesTo {

        private final Consumer<? super DidoData> consumer;
        private final KnownSchema<?, ?> owner;

        ValuesTo(Consumer<? super DidoData> consumer, KnownSchema<?, ?> owner) {
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
