package dido.data;

import dido.data.generic.AbstractGenericData;
import dido.data.generic.GenericData;
import dido.data.generic.GenericDataBuilders;

import java.util.*;

public class EnumMapData<E extends Enum<E>> extends AbstractGenericData<E> implements EnumData<E> {

    private final EnumSchema<E> schema;

    private final EnumMap<E, ?> map;

    private EnumMapData(EnumSchema<E> schema, EnumMap<E, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static <E extends Enum<E>> GenericData<E> from(EnumSchema<E> schema, EnumMap<E, ?> map) {
        return new EnumMapData<>(schema, new EnumMap<E, Object>(map));
    }

    @Override
    public Object getAt(int index) {
        return getOf(schema.getFieldAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
        return hasFieldOf(schema.getFieldAt(index));
    }

    @Override
    public Object get(String name) {
        return getOf(schema.getFieldNamed(name));
    }

    @Override
    public Object getOf(E field) {
        return map.get(field);
    }

    @Override
    public boolean hasFieldOf(E field) {
        return map.containsKey(field);
    }

    @Override
    public EnumSchema<E> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    public static <E extends Enum<E>> EnumData.Builder<E> newBuilder(EnumSchema<E> schema) {

        return new BuilderWithSchema<>(schema);
    }

    public static <E extends Enum<E>> EnumData.Builder<E> builderForEnum(Class<E> enumClass) {

        return new BuilderNoSchema<>(enumClass);
    }

    static class BuilderWithSchema<E extends Enum<E>>
            extends GenericDataBuilders.Fields<E, BuilderWithSchema<E>> implements EnumData.Builder<E> {

        private final EnumSchema<E> schema;

        private EnumMap<E, Object> map;

        BuilderWithSchema(EnumSchema<E> schema) {
            this.schema = Objects.requireNonNull(schema);
            this.map = new EnumMap<>(schema.getFieldType());
        }

        @Override
        public EnumData<E> build() {
            EnumData<E> data = new EnumMapData<>(schema, map);
            this.map = new EnumMap<>(schema.getFieldType());
            return data;
        }

        @Override
        public BuilderWithSchema<E> set(E field, Object value) {
            map.put(field, value);
            return this;
        }
    }

    static class BuilderNoSchema<E extends Enum<E>> implements EnumData.Builder<E> {

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
            EnumData<E> data = new EnumMapData<>(schema, map);
            this.typeMap = new HashMap<>();
            this.map = new EnumMap<>(enumClass);
            return data;
        }

        @Override
        public BuilderNoSchema<E> set(E field, Object value) {
            map.put(field, value);
            typeMap.put(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public EnumData.Builder<E> setBoolean(E field, boolean value) {
            map.put(field, value);
            typeMap.put(field, boolean.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setByte(E field, byte value) {
            map.put(field, value);
            typeMap.put(field, byte.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setChar(E field, char value) {
            map.put(field, value);
            typeMap.put(field, char.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setShort(E field, short value) {
            map.put(field, value);
            typeMap.put(field, short.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setInt(E field, int value) {
            map.put(field, value);
            typeMap.put(field, int.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setLong(E field, long value) {
            map.put(field, value);
            typeMap.put(field, long.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setFloat(E field, float value) {
            map.put(field, value);
            typeMap.put(field, float.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setDouble(E field, double value) {
            map.put(field, value);
            typeMap.put(field, double.class);
            return this;
        }

        @Override
        public EnumData.Builder<E> setString(E field, String value) {
            map.put(field, value);
            typeMap.put(field, String.class);
            return this;
        }
    }

}