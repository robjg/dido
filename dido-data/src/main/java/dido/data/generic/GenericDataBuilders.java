package dido.data.generic;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.IndexedDataBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

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


    public static class KnownSchema<F, D extends GenericData<F>, B extends KnownSchema<F, D, B>>
        extends Indexed<F, D, B> implements GenericDataBuilder<F> {

        private final GenericWritableSchema<F, D> schema;

        private final GenericDataFactory<F, D> dataFactory;

        protected KnownSchema(GenericWritableSchema<F, D> schema) {
            super();
            this.schema = Objects.requireNonNull(schema);
            this.dataFactory = schema.newDataFactory();
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
        public B withAt(int index, Object value) {
            dataFactory.getSetterAt(index).set(value);
            return self();
        }

        @Override
        public B with(F field, Object value) {
            dataFactory.getSetter(field).set(value);
            return self();
        }

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

        @Override
        public D build() {
            return dataFactory.toData();
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

    public static class BuilderNoSchema<F, D extends GenericData<F>> extends GenericDataBuilders.Fields<F, BuilderNoSchema<F, D>> {

        private final Map<F, Object> map = new LinkedHashMap<>();

        private final GenericWritableSchemaFactory<F, D> schemaBuilder;

        private final GenericSchemaField.Of<F> of;

        public BuilderNoSchema(GenericWritableSchemaFactory<F, D> writableSchemaFactory,
                               Function<? super String, ? extends F> fieldMappingFunc) {
            this.schemaBuilder = writableSchemaFactory;
            this.of = GenericSchemaField.with(fieldMappingFunc);
        }

        @Override
        public D build() {
            GenericWritableSchema<F, D> writableSchema = schemaBuilder.toSchema();

            GenericDataFactory<F, D> dataFactory = writableSchema.newDataFactory();

            for (F field : writableSchema.getFields()) {
                dataFactory.getSetter(field).set(map.get(field));
            }

            return dataFactory.toData();
        }

        @Override
        public BuilderNoSchema<F, D> with(F field, Object value) {
            map.put(field, value);

            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, value == null ? void.class : value.getClass()));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withBoolean(F field, boolean value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, boolean.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withByte(F field, byte value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, byte.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withChar(F field, char value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, char.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withShort(F field, short value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, short.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withInt(F field, int value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, int.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withLong(F field, long value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, long.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withFloat(F field, float value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, float.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withDouble(F field, double value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, double.class));
            return this;
        }

        @Override
        public BuilderNoSchema<F, D> withString(F field, String value) {
            map.put(field, value);
            schemaBuilder.addGenericSchemaField(of
                    .of(0, field, String.class));
            return this;
        }
    }
}
