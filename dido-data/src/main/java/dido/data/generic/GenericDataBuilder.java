package dido.data.generic;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.util.FieldValuesIn;

import java.util.*;
import java.util.function.Function;

/**
 * A Fluent builder for creating {@link GenericData} data ofr a Known schema from a {@link GenericDataFactory} or
 * from an Unknown schema from an {@link GenericDataFactoryProvider}.
 *
 * @param <F> The type of the fields.
 */
abstract public class GenericDataBuilder<F> {

    abstract public GenericDataBuilder<F> with(F field, Object value);

    abstract public GenericDataBuilder<F> withBoolean(F field, boolean value);

    abstract public GenericDataBuilder<F> withByte(F field, byte value);

    abstract public GenericDataBuilder<F> withChar(F field, char value);

    abstract public GenericDataBuilder<F> withShort(F field, short value);

    abstract public GenericDataBuilder<F> withInt(F field, int value);

    abstract public GenericDataBuilder<F> withLong(F field, long value);

    abstract public GenericDataBuilder<F> withFloat(F field, float value);

    abstract public GenericDataBuilder<F> withDouble(F field, double value);

    abstract public GenericDataBuilder<F> withString(F field, String value);

    abstract public GenericData<F> build();

    public static <F> GenericDataBuilder<F> forFactory(GenericDataFactory<F> dataFactory) {
        return new Known<>(dataFactory);
    }

    public static <F> GenericDataBuilder<F> forProvider(GenericDataFactoryProvider<F> dataFactoryProvider,
                                                        Function<? super String, ? extends F> fieldMappingFunc) {
        return new Unknown<>(dataFactoryProvider, fieldMappingFunc);
    }


    public static class Known<F, B extends Known<F, B>>
            extends GenericDataBuilder<F> {

        private final GenericDataFactory<F> dataFactory;

        private final Map<F, FieldSetter> setters;

        private final WritableData writableData;

        protected Known(GenericDataFactory<F> dataFactory) {
            this.dataFactory = dataFactory;
            GenericWriteSchema<F> schema = dataFactory.getSchema();

            Map<F, FieldSetter> setters = new HashMap<>();
            for (F field : schema.getFields()) {
                setters.put(field, schema.getFieldSetter(field));
            }
            this.setters = setters;
            this.writableData = dataFactory.getWritableData();
        }

        public GenericData<F> build() {
            return dataFactory.toData();
        }

        public B with(F field, Object value) {

            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .set(writableData, value);
            return self();
        }

        public B withBoolean(F field, boolean value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setBoolean(writableData, value);
            return self();
        }

        public B withByte(F field, byte value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setByte(writableData, value);
            return self();
        }

        public B withChar(F field, char value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setChar(writableData, value);
            return self();
        }

        public B withShort(F field, short value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setShort(writableData, value);
            return self();
        }

        public B withInt(F field, int value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setInt(writableData, value);
            return self();
        }

        public B withLong(F field, long value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setLong(writableData, value);
            return self();

        }

        public B withFloat(F field, float value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setFloat(writableData, value);
            return self();
        }

        public B withDouble(F field, double value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setDouble(writableData, value);
            return self();
        }

        public B withString(F field, String value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setString(writableData, value);
            return self();
        }

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }


    public static class Unknown<F, B extends Unknown<F, B>>
            extends GenericDataBuilder<F> {

        private final GenericDataFactoryProvider<F> factoryProvider;

        private final GenericSchemaField.Of<F> of;

        private final List<GenericSchemaField<F>> schemaFields = new ArrayList<>();

        private final List<Object> values = new ArrayList<>();


        public Unknown(GenericDataFactoryProvider<F> factoryProvider,
                       Function<? super String, ? extends F> fieldMappingFunc) {
            this.factoryProvider = factoryProvider;
            this.of = GenericSchemaField.with(fieldMappingFunc);
        }

        @SuppressWarnings("unchecked")
        @Override
        public GenericData<F> build() {

            SchemaFactory schemaFactory = factoryProvider.getSchemaFactory();
            for (SchemaField schemaField : schemaFields) {
                schemaFactory.addSchemaField(schemaField);
            }

            DataSchema schema = schemaFactory.toSchema();

            GenericDataFactory<F> dataFactory = factoryProvider.factoryFor(schema);

            return (GenericData<F>) FieldValuesIn.withDataFactory(dataFactory).ofCollection(values);
        }

        @Override
        public B with(F field, Object value) {

            values.add(value);
            schemaFields.add(of.of(0, field, value == null ? void.class : value.getClass()));
            return self();
        }

        @Override
        public B withBoolean(F field, boolean value) {

            values.add(value);
            schemaFields.add(of.of(0, field, boolean.class));
            return self();
        }

        @Override
        public B withByte(F field, byte value) {
            values.add(value);
            schemaFields.add(of.of(0, field, byte.class));
            return self();
        }

        @Override
        public B withChar(F field, char value) {
            values.add(value);
            schemaFields.add(of.of(0, field, char.class));
            return self();
        }

        @Override
        public B withShort(F field, short value) {
            values.add(value);
            schemaFields.add(of.of(0, field, short.class));
            return self();
        }

        @Override
        public B withInt(F field, int value) {
            values.add(value);
            schemaFields.add(of.of(0, field, int.class));
            return self();
        }

        @Override
        public B withLong(F field, long value) {
            values.add(value);
            schemaFields.add(of.of(0, field, long.class));
            return self();
        }

        @Override
        public B withFloat(F field, float value) {
            values.add(value);
            schemaFields.add(of.of(0, field, float.class));
            return self();
        }

        @Override
        public B withDouble(F field, double value) {
            values.add(value);
            schemaFields.add(of.of(0, field, double.class));
            return self();
        }

        @Override
        public B withString(F field, String value) {
            values.add(value);
            schemaFields.add(of.of(0, field, String.class));
            return self();
        }

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
