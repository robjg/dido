package dido.data;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 *
 * @param <F> The field type.
 */
public class ArrayData<F> extends AbstractGenericData<F> implements GenericData<F> {

    private final DataSchema<F> schema;

    private final Object[] data;

    private volatile int hash = 0;

    private ArrayData(DataSchema<F> schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static <T> GenericData<T> of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema<T> schema = new AbstractDataSchema<>() {

            @Override
            public SchemaField<T> getSchemaFieldAt(int index) {
                return SchemaFields.of(index, Object.class);
            }

            @Override
            public T getFieldAt(int index) {
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
            public int getIndex(T field) {
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
            public Collection<T> getFields() {
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

        return new ArrayData<>(schema, data);
    }

    public static <T> Builder<T> builderForSchema(DataSchema<T> schema) {

        return new Builder<>(schema);
    }

    public static <T> BuilderUnknown<T> newBuilder() {

        return new BuilderUnknown<>();
    }

    public static <F> DataBuilders.Values<F> valuesFor(DataSchema<F> schema) {

        return new Builder<>(schema).values();
    }

    @Override
    public DataSchema<F> getSchema() {
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

    public static class Builder<F> extends DataBuilders.KnownSchema<F, Builder<F>> {

        private Object[] values;

        Builder(DataSchema<F> schema) {
            super(schema);
            values = new Object[schema.lastIndex()];
        }

        public Builder<F> setAt(int index, Object value) {
            values[index - 1] = value;
            return this;
        }

        @Override
        public Builder<F> set(F field, Object value) {
            return setAt(getSchema().getIndex(field), value);
        }

        public GenericData<F> build() {
            Object[] values = this.values;
            this.values = new Object[getSchema().lastIndex()];
            return new ArrayData<>(getSchema(), values);
        }

        public GenericData<F> build(Object... values) {
            return new ArrayData<>(getSchema(), values);
        }
    }

    public static class BuilderUnknown<F> extends DataBuilders.Indexed<F, BuilderUnknown<F>>
            implements GenericDataBuilder<F> {

        private final List<SchemaField<F>> schemaFields = new LinkedList<>();

        private final List<Object> values = new LinkedList<>();

        private int lastIndex = 0;

        public BuilderUnknown<F> setAt(int index, Object value) {
            return setIndex(index, value, Object.class);
        }

        @Override
        public BuilderUnknown<F> setBooleanAt(int index, boolean value) {
            return setIndex(index, value, boolean.class);
        }

        @Override
        public BuilderUnknown<F> setByteAt(int index, byte value) {
            return setIndex(index, value, byte.class);
        }

        @Override
        public BuilderUnknown<F> setCharAt(int index, char value) {
            return setIndex(index, value, char.class);
        }

        @Override
        public BuilderUnknown<F> setShortAt(int index, short value) {
            return setIndex(index, value, short.class);
        }

        @Override
        public BuilderUnknown<F> setIntAt(int index, int value) {
            return setIndex(index, value, int.class);
        }

        @Override
        public BuilderUnknown<F> setLongAt(int index, long value) {
            return setIndex(index, value, long.class);
        }

        @Override
        public BuilderUnknown<F> setFloatAt(int index, float value) {
            return setIndex(index, value, float.class);
        }

        @Override
        public BuilderUnknown<F> setDoubleAt(int index, double value) {
            return setIndex(index, value, double.class);
        }

        @Override
        public BuilderUnknown<F> setStringAt(int index, String value) {
            return setIndex(index, value, String.class);
        }

        @Override
        public BuilderUnknown<F> set(F field, Object value) {
            return setField(field, value, Object.class);
        }

        @Override
        public BuilderUnknown<F> setBoolean(F field, boolean value) {
            return setField(field, value, boolean.class);
        }

        @Override
        public BuilderUnknown<F> setByte(F field, byte value) {
            return setField(field, value, byte.class);
        }

        @Override
        public BuilderUnknown<F> setChar(F field, char value) {
            return setField(field, value, char.class);
        }

        @Override
        public BuilderUnknown<F> setShort(F field, short value) {
            return setField(field, value, short.class);
        }

        @Override
        public BuilderUnknown<F> setInt(F field, int value) {
            return setField(field, value, int.class);
        }

        @Override
        public BuilderUnknown<F> setLong(F field, long value) {
            return setField(field, value, long.class);
        }

        @Override
        public BuilderUnknown<F> setFloat(F field, float value) {
            return setField(field, value, float.class);
        }

        @Override
        public BuilderUnknown<F> setDouble(F field, double value) {
            return setField(field, value, double.class);
        }

        @Override
        public BuilderUnknown<F> setString(F field, String value) {
            return setField(field, value, String.class);
        }

        public BuilderUnknown<F> setIndex(int index, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(index, type));
            if (index > lastIndex) {
                lastIndex = index;
            }
            return this;
        }

        private BuilderUnknown<F> setField(F field, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(++lastIndex, field, type));
            return this;
        }

        public GenericData<F> build() {
            SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
            Object[] values = new Object[lastIndex];
            Iterator<Object> valIt = this.values.iterator();
            for (SchemaField<F> schemaField : this.schemaFields) {
                schemaBuilder.addSchemaField(schemaField);
                values[schemaField.getIndex() - 1] = valIt.next();
            }
            return new ArrayData<>(schemaBuilder.build(), values);
        }
    }
}
