package dido.data.generic;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class GenericMapData<F> extends AbstractGenericData<F> {

    private final Schema<F> schema;

    private final Map<F, ?> map;

    private GenericMapData(Schema<F> schema, Map<F, ?> map) {
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

            GenericDataBuilders.BuilderNoSchema<F, GenericMapData<F>> builder =
                    new GenericDataBuilders.BuilderNoSchema<>(
                            new SchemaFactory<>(this), fieldNameMapping);
            for (int i = 0; i < args.length; i = i + 2) {
                //noinspection unchecked
                builder.with((F) args[i], args[i + 1]);
            }
            return builder.build();
        }

        public GenericDataSchema<F> schemaFromMap(Map<F, ?> map) {

            SchemaFactory<F> schemaFactory = new SchemaFactory<>(this);
            for (Map.Entry<F, ?> entry : map.entrySet()) {
                schemaFactory.addGenericSchemaField(schemaFactory.of()
                        .of(0, entry.getKey(), entry.getValue().getClass()));
            }
            return schemaFactory.toSchema();
        }

        public GenericWritableSchema<F, GenericMapData<F>> asGenericMapDataSchema(DataSchema schema) {

            if (schema instanceof Schema) {
                return (Schema<F>) schema;

            } else {

                return schemaFactory(schema).toSchema();
            }
        }

        public GenericDataBuilder<F> newBuilderNoSchema() {

            return new GenericDataBuilders.BuilderNoSchema<>(
                    new SchemaFactory<>(this), fieldNameMapping);
        }

        public GenericWritableSchemaFactory<F, GenericMapData<F>> schemaFactory() {

            return new SchemaFactory<>(this);
        }

        public GenericWritableSchemaFactory<F, GenericMapData<F>> schemaFactory(DataSchema schema) {

            SchemaFactory<F> factory = new SchemaFactory<>(this);
            for (SchemaField schemaField : schema.getSchemaFields()) {
                factory.addSchemaField(schemaField);
            }
            return factory;
        }

        public GenericDataBuilder<F> newBuilder(DataSchema schema) {

            return new GenericDataBuilders.KnownSchema<>(asGenericMapDataSchema(schema));
        }

        public GenericDataBuilders.Values<F> valuesFor(DataSchema schema) {

            return new GenericDataBuilders.KnownSchema<>(asGenericMapDataSchema(schema))
                    .values();
        }

        public GenericDataBuilder<F> copy(GenericData<F> from) {

            return new GenericDataBuilders.KnownSchema<>(asGenericMapDataSchema(from.getSchema()))
                    .copy(from);
        }

    }

    @Override
    public Object getAt(int index) {
        return get(schema.getFieldAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
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
    public GenericReadableSchema<F> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    static class Factory<F> implements GenericDataFactory<F, GenericMapData<F>> {

        private final Schema<F> schema;

        private Map<F, Object> data = new HashMap<>();

        Factory(Schema<F> schema) {
            this.schema = schema;
        }

        @Override
        public WritableSchema<GenericMapData<F>> getSchema() {
            return schema;
        }

        @Override
        public Setter getSetter(F field) {
            return new AbstractSetter() {
                @Override
                public void clear() {
                    data.remove(field);
                }

                @Override
                public void set(Object value) {
                    data.put(field, value);
                }
            };
        }

        @Override
        public Class<GenericMapData<F>> getDataType() {
            return null;
        }

        @Override
        public Setter getSetterAt(int index) {
            return null;
        }

        @Override
        public Setter getSetterNamed(String name) {
            return null;
        }

        @Override
        public DataSetter getSetter() {
            return null;
        }

        @Override
        public GenericMapData<F> toData() {
            GenericMapData<F> mapData = new GenericMapData<>(schema, data);
            this.data = new HashMap<>();
            return mapData;
        }
    }

    static class Schema<F> extends GenericSchemaImpl<F>
            implements GenericWritableSchema<F, GenericMapData<F>> {

        private final Of<F> of;

        protected Schema(Of<F> of,
                         GenericDataSchema<F> genericSchemaFields) {
            super(of.fieldType, genericSchemaFields);
            this.of = of;
        }

        protected Schema(Of<F> of,
                         Iterable<GenericSchemaField<F>> genericSchemaFields,
                         int firstIndex,
                         int lastIndex) {
            super(of.fieldType, genericSchemaFields, firstIndex, lastIndex);
            this.of = of;
        }

        @Override
        public GenericWritableSchemaFactory<F, GenericMapData<F>> newSchemaFactory() {
            return new SchemaFactory<>(of);
        }

        @Override
        public GenericDataFactory<F, GenericMapData<F>> newDataFactory() {
            return new Factory<>(this);
        }

        @Override
        public Getter getDataGetter(F field) {
            if (!Schema.this.hasField(field)) {
                throw new NoSuchFieldException(field.toString(), Schema.this);
            }
            return new AbstractGetter() {
                @Override
                public Object get(DidoData data) {
                    return ((GenericMapData<F>) data).get(field);
                }
            };
        }

        @Override
        public Getter getDataGetterAt(int index) {
            F field = Schema.this.getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, Schema.this);
            }
            return getDataGetter(field);
        }

        @Override
        public Getter getDataGetterNamed(String name) {
            F field = Schema.this.getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getDataGetter(field);
        }
    }


    static class SchemaFactory<F> extends GenericSchemaFactoryImpl<F, GenericWritableSchema<F, GenericMapData<F>>>
            implements GenericWritableSchemaFactory<F, GenericMapData<F>> {

        private final Of<F> of;

        SchemaFactory(Of<F> of) {
            super(of.fieldType, of.fieldNameMapping);
            this.of = of;
        }


        @Override
        protected GenericWritableSchema<F, GenericMapData<F>> createGeneric(Collection<GenericSchemaField<F>> genericSchemaFields,
                                                                            int firstIndex,
                                                                            int lastIndex) {
            return new Schema<>(of, genericSchemaFields, firstIndex, lastIndex);
        }
    }
}
