package dido.data;

import java.util.*;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class MapData<F> extends AbstractGenericData<F> {

    private final DataSchema<F> schema;

    private final Map<F, ?> map;

    private MapData(DataSchema<F> schema, Map<F, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static MapData<String> from(Map<String, ?> map) {
        return new MapData<>(schemaFromMap(map), new HashMap<>(map));
    }

    public static <F> GenericData<F> of() {
        return fromInputs();
    }

    public static <F> GenericData<F> of(F f1, Object v1) {
        return fromInputs(f1, v1);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2) {
        return fromInputs(f1, v1, f2, v2);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3) {
        return fromInputs(f1, v1, f2, v2, f3, v3);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5, F f6, Object v6) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                    F f7, Object v7) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                    F f7, Object v7, F f8, Object v8) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                    F f7, Object v7, F f8, Object v8, F f9, Object v9) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
    }

    public static <F> GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                    F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                    F f7, Object v7, F f8, Object v8, F f9, Object v9,
                                    F f10, Object v10) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
    }

    private static <F> GenericData<F> fromInputs(Object... args) {

        DataBuilder<F> builder = new BuilderNoSchema<>();
        for (int i = 0; i < args.length; i = i + 2) {
            //noinspection unchecked
            builder.set((F) args[i], args[i+1]);
        }
        return builder.build();
    }

    public static <F> DataSchema<F> schemaFromMap(Map<F, ?> map) {

        SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
        for (Map.Entry<F, ?> entry : map.entrySet()) {
            schemaBuilder.addField(entry.getKey(), entry.getValue().getClass());
        }
        return schemaBuilder.build();
    }

    public static <F> DataBuilder<F> newBuilder(DataSchema<F> schema) {

        return new BuilderWithSchema<>(schema);
    }

    public static DataBuilder<String> newBuilderNoSchema() {

        return new BuilderNoSchema<>();
    }

    public static <T> Values<T> valuesFor(DataSchema<T> schema) {

        return new Values<>(schema);
    }

    @Override
    public Object getAt(int index) {
        return get(schema.getFieldAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
        return hasField(schema.getFieldAt(index));
    }

    @Override
    public Object get(F field) {
        return map.get(field);
    }

    @Override
    public boolean hasField(F field) {
        return map.containsKey(field);
    }

    @Override
    public DataSchema<F> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    static class BuilderWithSchema<F> extends AbstractDataBuilder<F, BuilderWithSchema<F>> {

        private final DataSchema<F> schema;

        private Map<F, Object> map = new HashMap<>();

        BuilderWithSchema(DataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        @Override
        public GenericData<F> build() {
            GenericData<F> data = new MapData<>(schema, map);
            this.map = new HashMap<>();
            return data;
        }

        @Override
        public BuilderWithSchema<F> set(F field, Object value) {
            map.put(field, value);
            return this;
        }
    }

    static class BuilderNoSchema<F> implements DataBuilder<F> {

        private Map<F, Object> map = new LinkedHashMap<>();

        private SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

        @Override
        public GenericData<F> build() {
            GenericData<F> data = new MapData<>(schemaBuilder.build(), map);
            this.map = new LinkedHashMap<>();
            this.schemaBuilder = SchemaBuilder.impliedType();
            return data;
        }

        @Override
        public DataBuilder<F> set(F field, Object value) {
            map.put(field, value);
            schemaBuilder.addField(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public DataBuilder<F> setBoolean(F field, boolean value) {
            map.put(field, value);
            schemaBuilder.addField(field, boolean.class);
            return this;
        }

        @Override
        public DataBuilder<F> setByte(F field, byte value) {
            map.put(field, value);
            schemaBuilder.addField(field, byte.class);
            return this;
        }

        @Override
        public DataBuilder<F> setChar(F field, char value) {
            map.put(field, value);
            schemaBuilder.addField(field, char.class);
            return this;
        }

        @Override
        public DataBuilder<F> setShort(F field, short value) {
            map.put(field, value);
            schemaBuilder.addField(field, short.class);
            return this;
        }

        @Override
        public DataBuilder<F> setInt(F field, int value) {
            map.put(field, value);
            schemaBuilder.addField(field, int.class);
            return this;
        }

        @Override
        public DataBuilder<F> setLong(F field, long value) {
            map.put(field, value);
            schemaBuilder.addField(field, long.class);
            return this;
        }

        @Override
        public DataBuilder<F> setFloat(F field, float value) {
            map.put(field, value);
            schemaBuilder.addField(field, float.class);
            return this;
        }

        @Override
        public DataBuilder<F> setDouble(F field, double value) {
            map.put(field, value);
            schemaBuilder.addField(field, double.class);
            return this;
        }

        @Override
        public DataBuilder<F> setString(F field, String value) {
            map.put(field, value);
            schemaBuilder.addField(field, String.class);
            return this;
        }
    }

    public static class Values<F> {

        private final DataSchema<F> schema;

        Values(DataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        public GenericData<F> of(Object... values) {
            Map<F, Object> map = new HashMap<>();
            for (int i = 0; i < values.length; ++i) {
                F field = schema.getFieldAt(i + 1);
                if (field == null) {
                    throw new IllegalArgumentException("No field for index " + i + 1);
                }
                map.put(field, values[i]);
            }
            return new MapData<>(schema, map);
        }
    }
}
