package dido.data;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 *
 */
public class ArrayData extends AbstractData implements DidoData {

    private final DataSchema<String> schema;

    private final Object[] data;

    private volatile int hash = 0;

    private ArrayData(DataSchema<String> schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static DidoData of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema<String> schema = new AbstractDataSchema<>() {

            @Override
            public SchemaField<String> getSchemaFieldAt(int index) {
                return SchemaFields.of(index, Object.class);
            }

            @Override
            public String getFieldAt(int index) {
                return null;
            }

            @Override
            public Class<?> getTypeAt(int index) {
                return index > 0 && index <= data.length ? Object.class : null;
            }

            @Override
            public <N> DataSchema<N> getSchemaAt(int index) {
                return null;
            }

            @Override
            public int getIndex(String field) {
                return 0;
            }

            @Override
            public int firstIndex() {
                return data.length > 0 ? 1 : 0;
            }

            @Override
            public int nextIndex(int index) {
                return index > 0 && index < data.length ? index + 1 : 0;
            }

            @Override
            public int lastIndex() {
                return data.length;
            }

            @Override
            public Collection<String> getFields() {
                return Collections.emptyList();
            }

            @Override
            public int hashCode() {
                return DataSchema.hashCode(this);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof DataSchema) {
                    return DataSchema.equals(this, (DataSchema<?>) obj);
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return DataSchema.toString(this);
            }
        };

        return new ArrayData(schema, data);
    }

    public static Builder builderForSchema(DataSchema<String> schema) {

        return new Builder(schema);
    }

    public static BuilderUnknown newBuilder() {

        return new BuilderUnknown();
    }

    public static DataBuilders.Values valuesFor(DataSchema<String> schema) {

        return new Builder(schema).values();
    }

    @Override
    public DataSchema<String> getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return data[index -1];
    }

    @Override
    public boolean hasIndex(int index) {
        return data[index -1] != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
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
        return Arrays.toString(data);
    }

    public static class Builder extends DataBuilders.KnownSchema<Builder> {

        private Object[] values;

        Builder(DataSchema<String> schema) {
            super(schema);
            values = new Object[schema.lastIndex()];
        }

        public Builder setAt(int index, Object value) {
            values[index - 1] = value;
            return this;
        }

        @Override
        public Builder set(String field, Object value) {
            return setAt(getSchema().getIndex(field), value);
        }

        public DidoData build() {
            Object[] values = this.values;
            this.values = new Object[getSchema().lastIndex()];
            return new ArrayData(getSchema(), values);
        }

        public DidoData build(Object... values) {
            return new ArrayData(getSchema(), values);
        }
    }

    public static class BuilderUnknown extends DataBuilders.Indexed<BuilderUnknown>
            implements DataBuilder {

        private final List<SchemaField<String>> schemaFields = new LinkedList<>();

        private final List<Object> values = new LinkedList<>();

        private int lastIndex = 0;

        public BuilderUnknown setAt(int index, Object value) {
            return setIndex(index, value, Object.class);
        }

        @Override
        public BuilderUnknown setBooleanAt(int index, boolean value) {
            return setIndex(index, value, boolean.class);
        }

        @Override
        public BuilderUnknown setByteAt(int index, byte value) {
            return setIndex(index, value, byte.class);
        }

        @Override
        public BuilderUnknown setCharAt(int index, char value) {
            return setIndex(index, value, char.class);
        }

        @Override
        public BuilderUnknown setShortAt(int index, short value) {
            return setIndex(index, value, short.class);
        }

        @Override
        public BuilderUnknown setIntAt(int index, int value) {
            return setIndex(index, value, int.class);
        }

        @Override
        public BuilderUnknown setLongAt(int index, long value) {
            return setIndex(index, value, long.class);
        }

        @Override
        public BuilderUnknown setFloatAt(int index, float value) {
            return setIndex(index, value, float.class);
        }

        @Override
        public BuilderUnknown setDoubleAt(int index, double value) {
            return setIndex(index, value, double.class);
        }

        @Override
        public BuilderUnknown setStringAt(int index, String value) {
            return setIndex(index, value, String.class);
        }

        @Override
        public BuilderUnknown set(String field, Object value) {
            return setField(field, value, Object.class);
        }

        @Override
        public BuilderUnknown setBoolean(String field, boolean value) {
            return setField(field, value, boolean.class);
        }

        @Override
        public BuilderUnknown setByte(String field, byte value) {
            return setField(field, value, byte.class);
        }

        @Override
        public BuilderUnknown setChar(String field, char value) {
            return setField(field, value, char.class);
        }

        @Override
        public BuilderUnknown setShort(String field, short value) {
            return setField(field, value, short.class);
        }

        @Override
        public BuilderUnknown setInt(String field, int value) {
            return setField(field, value, int.class);
        }

        @Override
        public BuilderUnknown setLong(String field, long value) {
            return setField(field, value, long.class);
        }

        @Override
        public BuilderUnknown setFloat(String field, float value) {
            return setField(field, value, float.class);
        }

        @Override
        public BuilderUnknown setDouble(String field, double value) {
            return setField(field, value, double.class);
        }

        @Override
        public BuilderUnknown setString(String field, String value) {
            return setField(field, value, String.class);
        }

        public BuilderUnknown setIndex(int index, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(index, type));
            if (index > lastIndex) {
                lastIndex = index;
            }
            return this;
        }

        private BuilderUnknown setField(String field, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(++lastIndex, field, type));
            return this;
        }

        public DidoData build() {
            SchemaBuilder<String> schemaBuilder = SchemaBuilder.impliedType();
            Object[] values = new Object[lastIndex];
            Iterator<Object> valIt = this.values.iterator();
            for (SchemaField<String> schemaField : this.schemaFields) {
                schemaBuilder.addSchemaField(schemaField);
                values[schemaField.getIndex() - 1] = valIt.next();
            }
            return new ArrayData(schemaBuilder.build(), values);
        }
    }
}
