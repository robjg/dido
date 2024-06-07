package dido.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class MapData extends AbstractGenericData<String> implements DidoData {

    private final DataSchema<String> schema;

    private final Map<String, ?> map;

    private MapData(DataSchema<String> schema, Map<String, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static MapData from(Map<String, ?> map) {
        return new MapData(schemaFromMap(map), new HashMap<>(map));
    }

    public static DidoData of() {
        return fromInputs();
    }

    public static DidoData of(String f1, Object v1) {
        return fromInputs(f1, v1);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2) {
        return fromInputs(f1, v1, f2, v2);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3) {
        return fromInputs(f1, v1, f2, v2, f3, v3);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8, String f9, Object v9) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
    }

    public static DidoData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8, String f9, Object v9,
                                    String f10, Object v10) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
    }

    private static DidoData fromInputs(Object... args) {

        DataBuilder builder = new BuilderNoSchema();
        for (int i = 0; i < args.length; i = i + 2) {
            //noinspection unchecked
            builder.set((String) args[i], args[i+1]);
        }
        return builder.build();
    }

    public static DataSchema<String> schemaFromMap(Map<String, ?> map) {

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.impliedType();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            schemaBuilder.addField(entry.getKey(), entry.getValue().getClass());
        }
        return schemaBuilder.build();
    }

    public static DataBuilder newBuilder(DataSchema<String> schema) {

        return new BuilderWithSchema(schema);
    }

    public static DataBuilder newBuilderNoSchema() {

        return new BuilderNoSchema();
    }

    public static DataBuilders.Values valuesFor(DataSchema<String> schema) {

        return new BuilderWithSchema(schema).values();
    }

    public static BuilderWithSchema copy(IndexedData<String> from) {

        return new BuilderWithSchema(from.getSchema()).copy(from);
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
    public Object get(String field) {
        return map.get(field);
    }

    @Override
    public boolean hasField(String field) {
        return map.containsKey(field);
    }

    @Override
    public DataSchema<String> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    /**
     * A Builder that knows the schema. Setter don't validate the type.
     *
     */
    public static class BuilderWithSchema extends DataBuilders.KnownSchema<BuilderWithSchema> {

        private Map<String, Object> map = new HashMap<>();

        BuilderWithSchema(DataSchema<String> schema) {
            super(schema);
        }

        @Override
        public DidoData build() {
            DidoData data = new MapData(getSchema(), map);
            this.map = new HashMap<>();
            return data;
        }

        @Override
        public BuilderWithSchema set(String field, Object value) {
            map.put(field, value);
            return this;
        }

        @Override
        public BuilderWithSchema setAt(int index, Object value) {
            set(getSchema().getFieldAt(index), value);
            return this;
        }
    }

    static class BuilderNoSchema extends  DataBuilders.Fields<BuilderNoSchema> {

        private Map<String, Object> map = new LinkedHashMap<>();

        private SchemaBuilder<String> schemaBuilder = SchemaBuilder.impliedType();

        @Override
        public DidoData build() {
            DidoData data = new MapData(schemaBuilder.build(), map);
            this.map = new LinkedHashMap<>();
            this.schemaBuilder = SchemaBuilder.impliedType();
            return data;
        }

        @Override
        public BuilderNoSchema set(String field, Object value) {
            map.put(field, value);
            schemaBuilder.addField(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public BuilderNoSchema setBoolean(String field, boolean value) {
            map.put(field, value);
            schemaBuilder.addField(field, boolean.class);
            return this;
        }

        @Override
        public BuilderNoSchema setByte(String field, byte value) {
            map.put(field, value);
            schemaBuilder.addField(field, byte.class);
            return this;
        }

        @Override
        public BuilderNoSchema setChar(String field, char value) {
            map.put(field, value);
            schemaBuilder.addField(field, char.class);
            return this;
        }

        @Override
        public BuilderNoSchema setShort(String field, short value) {
            map.put(field, value);
            schemaBuilder.addField(field, short.class);
            return this;
        }

        @Override
        public BuilderNoSchema setInt(String field, int value) {
            map.put(field, value);
            schemaBuilder.addField(field, int.class);
            return this;
        }

        @Override
        public BuilderNoSchema setLong(String field, long value) {
            map.put(field, value);
            schemaBuilder.addField(field, long.class);
            return this;
        }

        @Override
        public BuilderNoSchema setFloat(String field, float value) {
            map.put(field, value);
            schemaBuilder.addField(field, float.class);
            return this;
        }

        @Override
        public BuilderNoSchema setDouble(String field, double value) {
            map.put(field, value);
            schemaBuilder.addField(field, double.class);
            return this;
        }

        @Override
        public BuilderNoSchema setString(String field, String value) {
            map.put(field, value);
            schemaBuilder.addField(field, String.class);
            return this;
        }

    }

    /**
     * For A fluent way of providing MapData
     */
    public static class Values {

        private final DataSchema<String> schema;

        Values(DataSchema<String> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        public GenericData<String> of(Object... values) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < values.length; ++i) {
                String field = schema.getFieldAt(i + 1);
                if (field == null) {
                    throw new IllegalArgumentException("No field for index " + i + 1);
                }
                map.put(field, values[i]);
            }
            return new MapData(schema, map);
        }
    }
}
