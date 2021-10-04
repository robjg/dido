package dido.data;

import java.util.*;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class MapData implements GenericData<String> {

    private final DataSchema<String> schema;

    private final Map<String, ?> map;

    private volatile int hash = 0;

    private MapData(DataSchema<String> schema, Map<String, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static MapData from(DataSchema<String> schema, Map<String, ?> map) {
        return new MapData(schema, map);
    }

    public static MapData from(Map<String, ?> map) {
        return new MapData(schemaFromMap(map), new HashMap<>(map));
    }

    public static DataSchema<String> schemaFromMap(Map<String, ?> map) {

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            schemaBuilder.addField(entry.getKey(), entry.getValue().getClass());
        }
        return schemaBuilder.build();
    }

    public static DataBuilder<String> newBuilder(DataSchema<String> schema) {

        return new BuilderWithSchema(schema);
    }

    public static DataBuilder<String> newBuilderNoSchema() {

        return new BuilderNoSchema();
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
    public boolean equals(Object o) {
        if (o instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) o);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = IndexedData.hashCode(this);
        }
        return hash;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    static class BuilderWithSchema extends AbstractDataBuilder<String, BuilderWithSchema> {

        private final DataSchema<String> schema;

        private Map<String, Object> map = new HashMap<>();

        BuilderWithSchema(DataSchema<String> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        @Override
        public GenericData<String> build() {
            GenericData<String> data = new MapData(schema, map);
            this.map = new HashMap<>();
            return data;
        }

        @Override
        public BuilderWithSchema set(String field, Object value) {
            map.put(field, value);
            return this;
        }
    }

    static class BuilderNoSchema implements DataBuilder<String> {

        private Map<String, Object> map = new LinkedHashMap<>();

        private SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        @Override
        public GenericData<String> build() {
            GenericData<String> data = new MapData(schemaBuilder.build(), map);
            this.map = new LinkedHashMap<>();
            this.schemaBuilder = SchemaBuilder.forStringFields();
            return data;
        }

        @Override
        public BuilderNoSchema set(String field, Object value) {
            map.put(field, value);
            schemaBuilder.addField(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public DataBuilder<String> setBoolean(String field, boolean value) {
            map.put(field, value);
            schemaBuilder.addField(field, boolean.class);
            return this;
        }

        @Override
        public DataBuilder<String> setByte(String field, byte value) {
            map.put(field, value);
            schemaBuilder.addField(field, byte.class);
            return this;
        }

        @Override
        public DataBuilder<String> setChar(String field, char value) {
            map.put(field, value);
            schemaBuilder.addField(field, char.class);
            return this;
        }

        @Override
        public DataBuilder<String> setShort(String field, short value) {
            map.put(field, value);
            schemaBuilder.addField(field, short.class);
            return this;
        }

        @Override
        public DataBuilder<String> setInt(String field, int value) {
            map.put(field, value);
            schemaBuilder.addField(field, int.class);
            return this;
        }

        @Override
        public DataBuilder<String> setLong(String field, long value) {
            map.put(field, value);
            schemaBuilder.addField(field, long.class);
            return this;
        }

        @Override
        public DataBuilder<String> setFloat(String field, float value) {
            map.put(field, value);
            schemaBuilder.addField(field, float.class);
            return this;
        }

        @Override
        public DataBuilder<String> setDouble(String field, double value) {
            map.put(field, value);
            schemaBuilder.addField(field, double.class);
            return this;
        }

        @Override
        public DataBuilder<String> setString(String field, String value) {
            map.put(field, value);
            schemaBuilder.addField(field, String.class);
            return this;
        }
    }
}
