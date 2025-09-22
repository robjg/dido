package dido.data.immutable;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.generic.GenericData;
import dido.data.schema.DataSchemaImpl;
import dido.data.schema.SchemaBuilder;
import dido.data.schema.SchemaFactoryImpl;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.AbstractFieldSetter;
import dido.data.useful.AbstractWritableData;
import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class MapData extends AbstractData implements DidoData {

    private final MapDataSchema schema;

    private final Map<String, ?> map;

    private MapData(MapDataSchema schema, Map<String, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static MapData from(Map<String, ?> map) {
        return new MapData(schemaFromMap(map), new HashMap<>(map));
    }

    public static MapData of() {
        return fromInputs();
    }

    public static MapData of(String f1, Object v1) {
        return fromInputs(f1, v1);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2) {
        return fromInputs(f1, v1, f2, v2);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3) {
        return fromInputs(f1, v1, f2, v2, f3, v3);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4, String f5, Object v5) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4, String f5, Object v5, String f6, Object v6) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4, String f5, Object v5, String f6, Object v6,
                               String f7, Object v7) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4, String f5, Object v5, String f6, Object v6,
                               String f7, Object v7, String f8, Object v8) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                               String f4, Object v4, String f5, Object v5, String f6, Object v6,
                               String f7, Object v7, String f8, Object v8, String f9, Object v9) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
    }

    public static MapData of(String f1, Object v1, String f2, Object v2, String f3, Object v3,
                          String f4, Object v4, String f5, Object v5, String f6, Object v6,
                          String f7, Object v7, String f8, Object v8, String f9, Object v9,
                          String f10, Object v10) {
        return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
    }

    private static MapData fromInputs(Object... args) {

        DataBuilder builder = builder();
        for (int i = 0; i < args.length; i = i + 2) {
            builder.with((String) args[i], args[i + 1]);
        }
        return (MapData) builder.build();
    }

    public static MapDataSchemaFactory schemaFactory() {
        return new MapDataSchemaFactory();
    }

    public static SchemaBuilder schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory());
    }

    public static MapDataSchema asMapDataSchema(DataSchema schema) {

        if (schema instanceof MapDataSchema) {
            return (MapDataSchema) schema;

        } else {
            return new MapDataSchema(schema);
        }
    }

    public static MapDataSchema schemaFromMap(Map<String, ?> map) {

        MapDataSchemaFactory schemaFactory = new MapDataSchemaFactory();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            schemaFactory.addSchemaField(SchemaField.of(0, entry.getKey(), entry.getValue().getClass()));
        }
        return schemaFactory.toSchema();
    }

    public static DataBuilder builderForSchema(DataSchema schema) {

        return DataBuilder.forFactory(factoryForSchema(schema));
    }

    public static DataBuilder builder() {

        return DataBuilder.forProvider(new MapDataDataFactoryProvider());
    }

    public static DataFactory factoryForSchema(DataSchema schema) {
        return new MapDataFactory(asMapDataSchema(schema));
    }

    public static FromValues withSchema(DataSchema schema) {

        return FieldValuesIn.withDataFactory(factoryForSchema(schema));
    }

    public static MapData copy(DidoData from) {

        return (MapData) withSchema(from.getSchema()).copy(from);
    }

    @Override
    public Object getAt(int index) {
        return this.getNamed(schema.getFieldNameAt(index));
    }

    @Override
    public boolean hasAt(int index) {
        return this.hasNamed(schema.getFieldNameAt(index));
    }

    @Override
    public Object getNamed(String name) {
        return map.get(name);
    }

    @Override
    public boolean hasNamed(String name) {
        return map.containsKey(name);
    }

    @Override
    public ReadSchema getSchema() {
        return schema;
    }



    private static class MapDataFactory extends AbstractWritableData implements DataFactory {

        private final MapDataSchema schema;

        private Map<String, Object> map;

        MapDataFactory(MapDataSchema schema) {
            this.schema = schema;
            this.map = new HashMap<>(schema.lastIndex());
        }

        @Override
        public MapDataSchema getSchema() {
            return schema;
        }

        @Override
        public void setAt(int index, Object value) {
            setNamed(schema.getFieldNameAt(index), value);
        }

        @Override
        public void clearAt(int index) {
            clearNamed(schema.getFieldNameAt(index));
        }

        @Override
        public void clearNamed(String name) {
            map.remove(name);
        }

        @Override
        public void setNamed(String name, Object value) {
            map.put(name, value);
        }

        @Override
        public WritableData getWritableData() {
            return this;
        }

        @Override
        public MapData toData() {
            MapData data = new MapData(schema, map);
            this.map = new HashMap<>(schema.lastIndex());
            return data;
        }
    }

    public static class MapDataSchema extends DataSchemaImpl
            implements ReadSchema, WriteSchema {

        MapDataSchema(DataSchema from) {
            super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
        }

        MapDataSchema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
            super(schemaFields, firstIndex, lastIndex);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {

            String name = getFieldNameAt(index);
            if (name == null) {
                throw new dido.data.NoSuchFieldException(index, this);
            }

            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return ((MapData) data).map.get(name);
                }
            };
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            if (!hasNamed(name)) {
                throw new NoSuchFieldException(name, this);
            }

            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return ((MapData) data).map.get(name);
                }
            };
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            String name = getFieldNameAt(index);
            return getFieldSetterNamed(name);
        }

        @Override
        public FieldSetter getFieldSetterNamed(String name) {

            return new AbstractFieldSetter() {
                @Override
                public void clear(WritableData writable) {
                    ((MapDataFactory) writable).map.remove(name);
                }

                @Override
                public void set(WritableData writable, Object value) {
                    ((MapDataFactory) writable).map.put(name, value);
                }
            };
        }
    }

    public static class MapDataSchemaFactory extends SchemaFactoryImpl<MapDataSchema> {

        protected MapDataSchemaFactory() {
        }

        protected MapDataSchemaFactory(DataSchema from) {
            super(from);
        }

        @Override
        protected MapDataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new MapDataSchema(fields, firstIndex, lastIndex);
        }
    }

}
