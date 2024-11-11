package dido.data.enums;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.generic.*;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.AbstractFieldSetter;

import java.util.Collection;
import java.util.EnumMap;
import java.util.function.Function;

public class EnumMapData<E extends Enum<E>> extends AbstractGenericData<E> implements EnumData<E> {

    private final EnumMapDataSchema<E> schema;

    private final EnumMap<E, ?> map;

    private EnumMapData(EnumMapDataSchema<E> schema, EnumMap<E, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static <E extends Enum<E>> Of<E> ofEnumClass(Class<E> type) {
        return new Of<>(type);
    }

    public static class Of<E extends Enum<E>> {

        private final Class<E> fieldType;

        private final Function<String, E> fieldNameMapping;

        public Of(Class<E> fieldType) {
            this.fieldType = fieldType;
            this.fieldNameMapping = name -> Enum.valueOf(fieldType, name);
        }

        @SuppressWarnings("unchecked")
        public EnumMapDataSchema<E> asEnumMapDataSchema(DataSchema schema) {

            if (schema instanceof EnumMapData.EnumMapDataSchema && ((EnumMapDataSchema<E>) schema).getFieldType() == this.fieldType) {
                return (EnumMapDataSchema<E>) schema;

            } else {

                return schemaFactory(schema).toSchema();
            }
        }

        public EnumMapDataSchemaFactory<E> schemaFactory() {
            return new EnumMapDataSchemaFactory<>(this);
        }

        public EnumMapDataSchemaFactory<E> schemaFactory(DataSchema schema) {

            EnumMapDataSchemaFactory<E> factory = new EnumMapDataSchemaFactory<>(this);
            for (SchemaField schemaField : schema.getSchemaFields()) {
                factory.addSchemaField(schemaField);
            }
            return factory;
        }

        public GenericDataFactory<E> factoryForSchema(DataSchema schema) {
            return new Factory<>(asEnumMapDataSchema(schema));
        }

        public GenericDataBuilder<E> builderForSchema(DataSchema schema) {

            return GenericDataBuilder.forFactory(factoryForSchema(
                    asEnumMapDataSchema(schema)));
        }

        public GenericDataBuilder<E> builderNoSchema() {

            return GenericDataBuilder.forProvider(EnumMapDataFactoryProvider.of(this),
                    fieldNameMapping);
        }
    }

    public static <E extends Enum<E>> GenericData<E> from(EnumSchema<E> schema, EnumMap<E, ?> map) {

        return new EnumMapData<>(
                new EnumMapDataSchema<>(schema.getFieldType(), schema),
                new EnumMap<E, Object>(map));
    }

    @Override
    public Object getAt(int index) {
        return this.get(schema.getFieldAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
        return has(schema.getFieldAt(index));
    }

    @Override
    public Object getNamed(String name) {
        return this.get(schema.getFieldNamed(name));
    }

    @Override
    public Object get(E field) {
        return map.get(field);
    }

    @Override
    public boolean has(E field) {
        return map.containsKey(field);
    }

    @Override
    public EnumReadSchema<E> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    public static <E extends Enum<E>> GenericDataBuilder<E> newBuilder(EnumSchema<E> schema) {

        return ofEnumClass(schema.getFieldType()).builderForSchema(schema);
    }

    public static <E extends Enum<E>> GenericDataBuilder<E> builderForEnum(Class<E> enumClass) {

        return ofEnumClass(enumClass).builderNoSchema();
    }

    static class Factory<E extends Enum<E>> extends AbstractGenericWritableData<E>
            implements GenericDataFactory<E>, GenericWritableData<E> {

        private final EnumMapDataSchema<E> schema;

        private EnumMap<E, Object> map;

        Factory(EnumMapDataSchema<E> schema) {
            this.schema = schema;
            this.map = new EnumMap<>(schema.getFieldType());
        }

        @Override
        public EnumMapDataSchema<E> getSchema() {
            return schema;
        }

        @Override
        public GenericWritableData<E> getWritableData() {
            return this;
        }

        @Override
        public EnumMapData<E> toData() {
            EnumMapData<E> data = new EnumMapData<>(schema, map);
            this.map = new EnumMap<>(schema.getFieldType());
            return data;
        }
    }

    public static class EnumMapDataSchema<E extends Enum<E>> extends GenericSchemaImpl<E>
            implements EnumReadSchema<E>, EnumWriteSchema<E> {

        protected EnumMapDataSchema(Class<E> enumClass, GenericDataSchema<E> schema) {
            super(enumClass, schema);
        }

        protected EnumMapDataSchema(Of<E> of, GenericDataSchema<E> schema) {
            super(of.fieldType, schema);
        }

        protected EnumMapDataSchema(Of<E> of,
                                    Iterable<GenericSchemaField<E>> genericSchemaFields,
                                    int firstIndex,
                                    int lastIndex) {
            super(of.fieldType, genericSchemaFields, firstIndex, lastIndex);
        }

        protected EnumMapDataSchema(Class<E> fieldType,
                                    Iterable<GenericSchemaField<E>> genericSchemaFields,
                                    int firstIndex,
                                    int lastIndex) {
            super(fieldType, genericSchemaFields, firstIndex, lastIndex);
        }

        @Override
        public FieldGetter getFieldGetter(E field) {
            if (hasField(field)) {
                return new AbstractFieldGetter() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Object get(DidoData data) {
                        return ((EnumMapData<E>) data).map.get(field);
                    }
                };
            } else {
                throw new NoSuchFieldException(field.toString(), this);
            }
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            E field = getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, this);
            } else {
                return getFieldGetter(field);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            E field = getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, this);
            } else {
                return getFieldGetter(field);
            }
        }

        @Override
        public FieldSetter getFieldSetter(E field) {
            if (hasField(field)) {
            return new AbstractFieldSetter() {
                @SuppressWarnings("unchecked")
                @Override
                public void clear(WritableData writable) {
                    ((Factory<E>) writable).map.remove(field);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void set(WritableData writable, Object value) {
                    ((Factory<E>) writable).map.put(field, value);
                }
            };
            }
            else {
                throw new NoSuchFieldException(field.toString(), this);
            }
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            E field = getFieldAt(index);
            if (field == null) {
                throw new NoSuchFieldException(index, this);
            } else {
                return getFieldSetter(field);
            }
        }

        @Override
        public FieldSetter getFieldSetterNamed(String name) {
            E field = getFieldNamed(name);
            if (field == null) {
                throw new NoSuchFieldException(name, this);
            } else {
                return getFieldSetter(field);
            }
        }
    }

    public static class EnumMapDataSchemaFactory<E extends Enum<E>>
            extends GenericSchemaFactoryImpl<E, EnumMapDataSchema<E>>
            implements GenericSchemaFactory<E> {

        private final Of<E> of;

        EnumMapDataSchemaFactory(Of<E> of) {
            super(of.fieldType, of.fieldNameMapping);
            this.of = of;
        }

        @Override
        protected EnumMapDataSchema<E> createGeneric(Collection<GenericSchemaField<E>> genericSchemaFields,
                                                     int firstIndex,
                                                     int lastIndex) {
            return new EnumMapDataSchema<>(of, genericSchemaFields, firstIndex, lastIndex);
        }
    }


}