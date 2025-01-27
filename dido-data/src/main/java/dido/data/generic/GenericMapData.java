package dido.data.generic;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.AbstractFieldSetter;
import dido.data.util.FieldValuesIn;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Provide an implementation of {@link GenericData} backed by a Map.
 */
public class GenericMapData<F> extends AbstractGenericData<F> {

    private final GenericMapDataSchema<F> schema;

    private final Map<F, ?> map;

    private GenericMapData(GenericMapDataSchema<F> schema, Map<F, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static <F> Of<F> with(Class<F> fieldType,
                                 Function<? super String, ? extends F> fieldNameMapping) {
        return new Of<>(fieldType, fieldNameMapping);
    }

    public static class Of<F> {

        private final Class<F> fieldType;

        private final Function<? super String, ? extends F> fieldNameMapping;

        public Of(Class<F> fieldType, Function<? super String, ? extends F> fieldNameMapping) {
            this.fieldType = fieldType;
            this.fieldNameMapping = fieldNameMapping;
        }

        public GenericData<F> of() {
            return fromInputs();
        }

        public GenericData<F> of(F f1, Object v1) {
            return fromInputs(f1, v1);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2) {
            return fromInputs(f1, v1, f2, v2);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3) {
            return fromInputs(f1, v1, f2, v2, f3, v3);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8, F f9, Object v9) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8, F f9, Object v9,
                                 F f10, Object v10) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
        }

        private GenericData<F> fromInputs(Object... args) {

            GenericDataBuilder<F> builder = builderNoSchema();
            for (int i = 0; i < args.length; i = i + 2) {
                //noinspection unchecked
                builder.with((F) args[i], args[i + 1]);
            }
            return builder.build();
        }

        public GenericDataSchema<F> schemaFromMap(Map<F, ?> map) {

            GenericMapDataSchemaFactory<F> schemaFactory = new GenericMapDataSchemaFactory<>(this);
            for (Map.Entry<F, ?> entry : map.entrySet()) {
                schemaFactory.addGenericSchemaField(schemaFactory.of()
                        .of(0, entry.getKey(), entry.getValue().getClass()));
            }
            return schemaFactory.toSchema();
        }

        @SuppressWarnings("unchecked")
        public GenericMapDataSchema<F> asGenericMapDataSchema(DataSchema schema) {

            if (schema instanceof GenericMapData.GenericMapDataSchema && ((GenericMapDataSchema<F>) schema).getFieldType() == this.fieldType) {
                return (GenericMapDataSchema<F>) schema;

            } else {

                return schemaFactory(schema).toSchema();
            }
        }

        public GenericSchemaFactory<F> schemaFactory() {

            return new GenericMapDataSchemaFactory<>(this);
        }

        public GenericMapDataSchemaFactory<F> schemaFactory(DataSchema schema) {

            GenericMapDataSchemaFactory<F> factory = new GenericMapDataSchemaFactory<>(this);
            for (SchemaField schemaField : schema.getSchemaFields()) {
                factory.addSchemaField(schemaField);
            }
            return factory;
        }

        public FieldValuesIn valuesFor(DataSchema schema) {

            return FieldValuesIn.withDataFactory(factoryForSchema(schema));
        }

        public GenericDataFactory<F> factoryForSchema(DataSchema schema) {
            return new Factory<>(asGenericMapDataSchema(schema));
        }

        public GenericDataBuilder<F> builderForSchema(DataSchema schema) {

            return GenericDataBuilder.forFactory(factoryForSchema(
                    asGenericMapDataSchema(schema)));
        }

        public GenericDataBuilder<F> builderNoSchema() {

            return GenericDataBuilder.forProvider(new GenericMapDataFactoryProvider<>(this), fieldNameMapping);
        }
    }


    @Override
    public Object getAt(int index) {
        return get(schema.getFieldAt(index));
    }

    @Override
    public boolean hasAt(int index) {
        return this.has(schema.getFieldAt(index));
    }

    @Override
    public Object get(F field) {
        return map.get(field);
    }

    @Override
    public boolean has(F field) {
        return map.containsKey(field);
    }

    @Override
    public GenericReadSchema<F> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    static class Factory<F> extends AbstractGenericWritableData<F>
            implements GenericDataFactory<F> {

        private final GenericMapDataSchema<F> schema;

        private Map<F, Object> data = new HashMap<>();

        Factory(GenericMapDataSchema<F> schema) {
            this.schema = schema;
        }

        @Override
        public GenericMapDataSchema<F> getSchema() {
            return schema;
        }

        @Override
        public GenericWritableData<F> getWritableData() {
            return this;
        }

        @Override
        public void clear(F field) {
            data.remove(field);
        }

        @Override
        public void set(F field, Object value) {
            data.put(field, value);
        }

        @Override
        public GenericMapData<F> toData() {
            GenericMapData<F> mapData = new GenericMapData<>(schema, data);
            this.data = new HashMap<>();
            return mapData;
        }
    }

    public static class GenericMapDataSchema<F> extends GenericSchemaImpl<F>
            implements  GenericReadSchema<F>, GenericWriteSchema<F> {

        private final Of<F> of;

        protected GenericMapDataSchema(Of<F> of,
                                       GenericDataSchema<F> schema) {
            super(of.fieldType, schema);
            this.of = of;
        }

        protected GenericMapDataSchema(Of<F> of,
                                       Iterable<GenericSchemaField<F>> genericSchemaFields,
                                       int firstIndex,
                                       int lastIndex) {
            super(of.fieldType, genericSchemaFields, firstIndex, lastIndex);
            this.of = of;
        }

        @Override
        public FieldGetter getFieldGetter(F field) {
            if (!GenericMapDataSchema.this.hasField(field)) {
                throw new NoSuchFieldException(field.toString(), GenericMapDataSchema.this);
            }
            return new AbstractFieldGetter() {
                @SuppressWarnings("unchecked")
                @Override
                public Object get(DidoData data) {
                    return ((GenericMapData<F>) data).get(field);
                }
            };
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            F field = GenericMapDataSchema.this.getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, GenericMapDataSchema.this);
            }
            return getFieldGetter(field);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            F field = GenericMapDataSchema.this.getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, GenericMapDataSchema.this);
            }
            return getFieldGetter(field);
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            F field = getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, this);
            }
            return getFieldSetter(field);
        }

        @Override
        public FieldSetter getFieldSetterNamed(String name) {
            F field = GenericMapDataSchema.this.getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, GenericMapDataSchema.this);
            }
            return getFieldSetter(field);
        }

        @Override
        public FieldSetter getFieldSetter(F field) {
            if (!hasField(field)) {
                throw new NoSuchFieldException(field.toString(), this);
            }

            return new AbstractFieldSetter() {
                @SuppressWarnings("unchecked")
                @Override
                public void clear(WritableData writable) {
                    ((Factory<F>) writable).data.remove(field);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void set(WritableData writable, Object value) {
                    ((Factory<F>) writable).data.put(field, value);
                }
            };
        }
    }

    public static class GenericMapDataSchemaFactory<F> extends GenericSchemaFactoryImpl<F, GenericMapDataSchema<F>>
            implements GenericSchemaFactory<F> {

        private final Of<F> of;

        GenericMapDataSchemaFactory(Of<F> of) {
            super(of.fieldType, of.fieldNameMapping);
            this.of = of;
        }

        @Override
        protected GenericMapDataSchema<F> createGeneric(Collection<GenericSchemaField<F>> genericSchemaFields,
                                                        int firstIndex,
                                                        int lastIndex) {
            return new GenericMapDataSchema<>(of, genericSchemaFields, firstIndex, lastIndex);
        }
    }
}
