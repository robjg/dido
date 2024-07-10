package dido.data;

import dido.data.generic.GenericData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class MapData extends AbstractNamedData implements NamedData {

    private final DataSchema schema;

    private final Map<String, ?> map;

    private MapData(DataSchema schema, Map<String, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static MapData from(Map<String, ?> map) {
        return new MapData(schemaFromMap(map), new HashMap<>(map));
    }

    public static NamedData of() {
        return fromInputs();
    }

    public static NamedData of(String f1, Object v1) {
        return fromInputs(f1, v1);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2) {
        return fromInputs(f1, v1, f2, v2);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3) {
        return fromInputs(f1, v1, f2, v2, f3, v3);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8, String f9, Object v9) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
    }

    public static NamedData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                                    String f4, Object v4, String f5, Object v5, String f6, Object v6,
                                    String f7, Object v7, String f8, Object v8, String f9, Object v9,
                                    String f10, Object v10) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
    }

    private static NamedData fromInputs(Object... args) {

        NamedDataBuilder builder = new BuilderNoSchema();
        for (int i = 0; i < args.length; i = i + 2) {
            builder.with((String) args[i], args[i+1]);
        }
        return builder.build();
    }

    public static DataSchema schemaFromMap(Map<String, ?> map) {

        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            schemaBuilder.addNamed(entry.getKey(), entry.getValue().getClass());
        }
        return schemaBuilder.build();
    }

    public static NamedDataBuilder newBuilder(DataSchema schema) {

        return new BuilderWithSchema(schema);
    }

    public static NamedDataBuilder newBuilderNoSchema() {

        return new BuilderNoSchema();
    }

    public static DataBuilders.NamedValues valuesFor(DataSchema schema) {

        return new BuilderWithSchema(schema).values();
    }

    public static BuilderWithSchema copy(DidoData from) {

        return new BuilderWithSchema(from.getSchema()).copy(from);
    }

    public static DataFactory<NamedData> factoryFor(DataSchema schema) {
        return new MapDataFactory(schema);
    }

    @Override
    public Object getAt(int index) {
        return get(schema.getFieldNameAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
        return has(schema.getFieldNameAt(index));
    }

    @Override
    public Object get(String name) {
        return map.get(name);
    }

    @Override
    public boolean has(String name) {
        return map.containsKey(name);
    }

    @Override
    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return DidoData.toStringFieldsOnly(this);
    }

    /**
     * A Builder that knows the schema. Setter don't validate the type.
     *
     */
    public static class BuilderWithSchema extends DataBuilders.NamedKnownSchema<BuilderWithSchema> {

        private Map<String, Object> map = new HashMap<>();

        BuilderWithSchema(DataSchema schema) {
            super(schema);
        }

        @Override
        public NamedData build() {
            NamedData data = new MapData(getSchema(), map);
            this.map = new HashMap<>();
            return data;
        }

        @Override
        public BuilderWithSchema with(String field, Object value) {
            map.put(field, value);
            return this;
        }
    }

    static class BuilderNoSchema extends DataBuilders.NamedFields<BuilderNoSchema> {

        private Map<String, Object> map = new LinkedHashMap<>();

        private SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        @Override
        public NamedData build() {
            NamedData data = new MapData(schemaBuilder.build(), map);
            this.map = new LinkedHashMap<>();
            this.schemaBuilder = SchemaBuilder.newInstance();
            return data;
        }

        @Override
        public BuilderNoSchema with(String field, Object value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public BuilderNoSchema withBoolean(String field, boolean value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, boolean.class);
            return this;
        }

        @Override
        public BuilderNoSchema withByte(String field, byte value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, byte.class);
            return this;
        }

        @Override
        public BuilderNoSchema withChar(String field, char value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, char.class);
            return this;
        }

        @Override
        public BuilderNoSchema withShort(String field, short value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, short.class);
            return this;
        }

        @Override
        public BuilderNoSchema withInt(String field, int value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, int.class);
            return this;
        }

        @Override
        public BuilderNoSchema withLong(String field, long value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, long.class);
            return this;
        }

        @Override
        public BuilderNoSchema withFloat(String field, float value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, float.class);
            return this;
        }

        @Override
        public BuilderNoSchema withDouble(String field, double value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, double.class);
            return this;
        }

        @Override
        public BuilderNoSchema withString(String field, String value) {
            map.put(field, value);
            schemaBuilder.addNamed(field, String.class);
            return this;
        }

    }

    static class MapDataFactory extends AbstractDataSetter implements DataFactory<NamedData> {

        private final DataSchema schema;

        private Map<String, Object> map;

        MapDataFactory(DataSchema schema) {
            this.schema = schema;
            this.map = new HashMap<>(schema.lastIndex());
        }

        @Override
        public void clearNamed(String name) {
            map.remove(name);
        }

        @Override
        public void setNamed(String field, Object value) {
            map.put(field, value);
        }

        @Override
        public Class<NamedData> getDataType() {
            return NamedData.class;
        }

        @Override
        public Setter getSetterAt(int index) {
            String name = schema.getFieldNameAt(index);
            return getSetterNamed(name);
        }

        @Override
        public Setter getSetterNamed(String name) {
            return new AbstractSetter() {
                @Override
                public void clear() {
                    clearNamed(name);
                }

                @Override
                public void set(Object value) {
                    setNamed(name, value);
                }
            };
        }

        @Override
        public DataSetter getSetter() {
            return this;
        }

        @Override
        public void copy(NamedData data) {
            throw new UnsupportedOperationException("TODO");
        }

        @Override
        public NamedData valuesToData(Object... values) {
            Map<String, Object> map = new HashMap<>(values.length);
            for (int i = 0; i < values.length; ++i ) {
                map.put(schema.getFieldNameAt(i + 1), values[i]);
            }
            return new MapData(schema, map);
        }

        @Override
        public NamedData toData() {
            NamedData data = new MapData(schema, map);
            this.map = new HashMap<>(schema.lastIndex());
            return data;
        }

    }

}
