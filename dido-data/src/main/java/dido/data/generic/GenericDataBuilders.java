package dido.data.generic;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.util.FieldValuesIn;

import java.util.*;
import java.util.function.Function;

/**
 * Base class for {@link GenericDataBuilder}s that convert primitives into Objects.
 */
abstract public class GenericDataBuilders {

    public static class KnownSchema<F, D extends GenericData<F>, B extends KnownSchema<F, D, B>>
            implements GenericDataBuilder<F> {

        private final GenericDataFactory<F, D> dataFactory;

        private final Map<F, FieldSetter> setters;

        private final WritableData writableData;

        protected KnownSchema(GenericDataFactory<F, D> dataFactory) {
            this.dataFactory = dataFactory;
            GenericWriteSchema<F> schema = dataFactory.getSchema();

            Map<F, FieldSetter> setters = new HashMap<>();
            for (F field : schema.getFields()) {
                setters.put(field, schema.getFieldSetter(field));
            }
            this.setters = setters;
            this.writableData = dataFactory.getWritableData();
        }

        public D build() {
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

        public B  withByte(F field, byte value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setByte(writableData, value);
            return self();
        }

        public B  withChar(F field, char value) {
            Optional.ofNullable(setters.get(field))
                    .orElseThrow(() -> new NoSuchFieldException(field.toString(), dataFactory.getSchema()))
                    .setChar(writableData, value);
            return self();
        }

        public B  withShort(F field, short value) {
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


    public static class BuilderNoSchema<F, D extends GenericData<F>, B extends BuilderNoSchema<F, D, B>>
            implements GenericDataBuilder<F> {

        private final DataFactoryProvider<D> factoryProvider;

        private final GenericSchemaField.Of<F> of;

        private final List<GenericSchemaField<F>> schemaFields = new ArrayList<>();

        private final List<Object> values = new ArrayList<>();


        public BuilderNoSchema(DataFactoryProvider<D> factoryProvider,
                               Function<? super String, ? extends F> fieldMappingFunc) {
            this.factoryProvider = factoryProvider;
            this.of = GenericSchemaField.with(fieldMappingFunc);
        }

        @Override
        public D build() {

            SchemaFactory schemaFactory = factoryProvider.getSchemaFactory();
            for (SchemaField schemaField : schemaFields) {
                schemaFactory.addSchemaField(schemaField);
            }

            DataSchema schema = schemaFactory.toSchema();

            DataFactory<D> dataFactory = factoryProvider.provideFactory(schema);

            return FieldValuesIn.withDataFactory(dataFactory).ofCollection(values);
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
