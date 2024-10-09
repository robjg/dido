package dido.data.enums;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.generic.*;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.AbstractFieldSetter;

import java.util.*;
import java.util.function.Function;

public class EnumMapData<E extends Enum<E>> extends AbstractGenericData<E> implements EnumData<E> {

    private final Schema<E> schema;

    private final EnumMap<E, ?> map;

    private EnumMapData(Schema<E> schema, EnumMap<E, ?> map) {
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
        public Schema<E> asEnumMapDataSchema(DataSchema schema) {

            if (schema instanceof Schema && ((Schema<E>) schema).getFieldType() == this.fieldType) {
                return (Schema<E>) schema;

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

        public GenericDataFactory<E, EnumMapData<E>> factoryForSchema(DataSchema schema) {
            return new Factory<>(asEnumMapDataSchema(schema));
        }
    }


    public static <E extends Enum<E>> GenericData<E> from(EnumSchema<E> schema, EnumMap<E, ?> map) {

        return new EnumMapData<>(
                new Schema<>(schema.getFieldType(), schema),
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

    public static <E extends Enum<E>> EnumDataBuilder<E> newBuilder(EnumSchema<E> schema) {

        return new BuilderWithSchema<>(ofEnumClass(schema.getFieldType()).factoryForSchema(schema));
    }

    public static <E extends Enum<E>> EnumDataBuilder<E> builderForEnum(Class<E> enumClass) {

        return new BuilderNoSchema<>(enumClass);
    }

    static class Factory<E extends Enum<E>> extends AbstractGenericWritableData<E>
            implements GenericDataFactory<E, EnumMapData<E>>, GenericWritableData<E> {

        private final Schema<E> schema;

        private EnumMap<E, Object> map;

        Factory(Schema<E> schema) {
            this.schema = schema;
            this.map = new EnumMap<>(schema.getFieldType());
        }

        @Override
        public GenericReadWriteSchema<E> getSchema() {
            return schema;
        }

        @Override
        public Class<E> getDataType() {
            return schema.getFieldType();
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

    public static class Schema<E extends Enum<E>> extends GenericSchemaImpl<E>
            implements EnumReadWriteSchema<E> {

        protected Schema(Class<E> enumClass, GenericDataSchema<E> schema) {
            super(enumClass, schema);
        }

        protected Schema(Of<E> of, GenericDataSchema<E> schema) {
            super(of.fieldType, schema);
        }

        protected Schema(Of<E> of,
                         Iterable<GenericSchemaField<E>> genericSchemaFields,
                         int firstIndex,
                         int lastIndex) {
            super(of.fieldType, genericSchemaFields, firstIndex, lastIndex);
        }

        protected Schema(Class<E> fieldType,
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
            extends GenericSchemaFactoryImpl<E, Schema<E>>
            implements GenericSchemaFactory<E> {

        private final Of<E> of;

        EnumMapDataSchemaFactory(Of<E> of) {
            super(of.fieldType, of.fieldNameMapping);
            this.of = of;
        }

        @Override
        protected Schema<E> createGeneric(Collection<GenericSchemaField<E>> genericSchemaFields,
                                          int firstIndex,
                                          int lastIndex) {
            return new Schema<>(of, genericSchemaFields, firstIndex, lastIndex);
        }
    }


    static class BuilderWithSchema<E extends Enum<E>>
            extends GenericDataBuilders.KnownSchema<E, EnumMapData<E>, BuilderWithSchema<E>>
            implements EnumDataBuilder<E> {

        BuilderWithSchema(GenericDataFactory<E, EnumMapData<E>> dataFactory) {
            super(dataFactory);
        }

    }


    static class BuilderNoSchema<E extends Enum<E>> implements EnumDataBuilder<E> {

        private final Class<E> enumClass;

        private EnumMap<E, Object> map;

        private Map<E, Class<?>> typeMap;

        BuilderNoSchema(Class<E> enumClass) {
            this.enumClass = enumClass;
            this.typeMap = new HashMap<>();
            this.map = new EnumMap<>(enumClass);
        }

        @Override
        public EnumData<E> build() {
            @SuppressWarnings({"unchecked", "rawtypes"}) EnumSchema<E> schema = EnumSchema.schemaFor(enumClass, e ->
                    Optional.ofNullable(typeMap.get(e)).orElse((Class) void.class));
            EnumData<E> data = new EnumMapData<>(
                    new Schema<>(schema.getFieldType(), schema), map);
            this.typeMap = new HashMap<>();
            this.map = new EnumMap<>(enumClass);
            return data;
        }

        @Override
        public BuilderNoSchema<E> with(E field, Object value) {
            map.put(field, value);
            typeMap.put(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public EnumDataBuilder<E> withBoolean(E field, boolean value) {
            map.put(field, value);
            typeMap.put(field, boolean.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withByte(E field, byte value) {
            map.put(field, value);
            typeMap.put(field, byte.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withChar(E field, char value) {
            map.put(field, value);
            typeMap.put(field, char.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withShort(E field, short value) {
            map.put(field, value);
            typeMap.put(field, short.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withInt(E field, int value) {
            map.put(field, value);
            typeMap.put(field, int.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withLong(E field, long value) {
            map.put(field, value);
            typeMap.put(field, long.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withFloat(E field, float value) {
            map.put(field, value);
            typeMap.put(field, float.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withDouble(E field, double value) {
            map.put(field, value);
            typeMap.put(field, double.class);
            return this;
        }

        @Override
        public EnumDataBuilder<E> withString(E field, String value) {
            map.put(field, value);
            typeMap.put(field, String.class);
            return this;
        }
    }

}