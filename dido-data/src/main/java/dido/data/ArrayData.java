package dido.data;

import dido.data.generic.GenericData;
import dido.data.generic.GenericSchemaField;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 *
 */
public class ArrayData extends AbstractNamedData implements NamedData {

    private final DataSchema schema;

    private final Object[] data;

    private ArrayData(DataSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static DidoData of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema schema = new AbstractDataSchema() {

            @Override
            public SchemaField getSchemaFieldAt(int index) {
                return index > 0 && index <= data.length ?
                        SchemaFields.of(index, Object.class) : null;
            }

            @Override
            public int getIndexNamed(String fieldName) {
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
            public Collection<String> getFieldNames() {
                return Collections.emptyList();
            }

        };

        return new ArrayData(schema, data);
    }

    public static Builder builderForSchema(DataSchema schema) {

        return new Builder(schema);
    }

    public static BuilderUnknown newBuilder() {

        return new BuilderUnknown();
    }

    public static DataBuilders.Values valuesFor(DataSchema schema) {

        return new Builder(schema).values();
    }

    public static DataFactory<NamedData> factoryFor(DataSchema schema) {
        return new ArrayDataFactory(schema);
    }

    @Override
    public DataSchema getSchema() {
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
    public String toString() {
        return Arrays.toString(data);
    }

    public static class Builder extends DataBuilders.KnownSchema<Builder> {

        private Object[] values;

        Builder(DataSchema schema) {
            super(schema);
            values = new Object[schema.lastIndex()];
        }

        public Builder withAt(int index, Object value) {
            values[index - 1] = value;
            return this;
        }

        @Override
        public Builder with(String field, Object value) {
            return withAt(getSchema().getIndexNamed(field), value);
        }

        public NamedData build() {
            Object[] values = this.values;
            this.values = new Object[getSchema().lastIndex()];
            return new ArrayData(getSchema(), values);
        }

        public DidoData build(Object... values) {
            return new ArrayData(getSchema(), values);
        }
    }

    public static class BuilderUnknown
            extends DataBuilders.Indexed<NamedData, BuilderUnknown>
            implements NamedDataBuilder {

        private final List<GenericSchemaField<String>> schemaFields = new LinkedList<>();

        private final List<Object> values = new LinkedList<>();

        private int lastIndex = 0;


        public BuilderUnknown withAt(int index, Object value) {
            return setIndex(index, value, Object.class);
        }

        @Override
        public BuilderUnknown withBooleanAt(int index, boolean value) {
            return setIndex(index, value, boolean.class);
        }

        @Override
        public BuilderUnknown withByteAt(int index, byte value) {
            return setIndex(index, value, byte.class);
        }

        @Override
        public BuilderUnknown withCharAt(int index, char value) {
            return setIndex(index, value, char.class);
        }

        @Override
        public BuilderUnknown withShortAt(int index, short value) {
            return setIndex(index, value, short.class);
        }

        @Override
        public BuilderUnknown withIntAt(int index, int value) {
            return setIndex(index, value, int.class);
        }

        @Override
        public BuilderUnknown withLongAt(int index, long value) {
            return setIndex(index, value, long.class);
        }

        @Override
        public BuilderUnknown withFloatAt(int index, float value) {
            return setIndex(index, value, float.class);
        }

        @Override
        public BuilderUnknown withDoubleAt(int index, double value) {
            return setIndex(index, value, double.class);
        }

        @Override
        public BuilderUnknown withStringAt(int index, String value) {
            return setIndex(index, value, String.class);
        }

        @Override
        public BuilderUnknown with(String field, Object value) {
            return setField(field, value, Object.class);
        }

        @Override
        public BuilderUnknown withBoolean(String field, boolean value) {
            return setField(field, value, boolean.class);
        }

        @Override
        public BuilderUnknown withByte(String field, byte value) {
            return setField(field, value, byte.class);
        }

        @Override
        public BuilderUnknown withChar(String field, char value) {
            return setField(field, value, char.class);
        }

        @Override
        public BuilderUnknown withShort(String field, short value) {
            return setField(field, value, short.class);
        }

        @Override
        public BuilderUnknown withInt(String field, int value) {
            return setField(field, value, int.class);
        }

        @Override
        public BuilderUnknown withLong(String field, long value) {
            return setField(field, value, long.class);
        }

        @Override
        public BuilderUnknown withFloat(String field, float value) {
            return setField(field, value, float.class);
        }

        @Override
        public BuilderUnknown withDouble(String field, double value) {
            return setField(field, value, double.class);
        }

        @Override
        public BuilderUnknown withString(String field, String value) {
            return setField(field, value, String.class);
        }


        public BuilderUnknown setIndex(int index, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(GenericSchemaField.of(index, type));
            if (index > lastIndex) {
                lastIndex = index;
            }
            return this;
        }

        private BuilderUnknown setField(String field, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(GenericSchemaField.of(++lastIndex, field, type));
            return this;
        }

        public NamedData build() {
            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();
            Object[] values = new Object[lastIndex];
            Iterator<Object> valIt = this.values.iterator();
            for (SchemaField schemaField : this.schemaFields) {
                schemaBuilder.addSchemaField(schemaField);
                values[schemaField.getIndex() - 1] = valIt.next();
            }
            return new ArrayData(schemaBuilder.build(), values);
        }
    }

    static class ArrayDataFactory extends AbstractIndexedSetter implements DataFactory<NamedData> {

        private final DataSchema schema;

        private Object[] values;

        ArrayDataFactory(DataSchema schema) {
            this.schema = schema;
            values = new Object[schema.lastIndex()];
        }

        @Override
        public Class<NamedData> getDataType() {
            return null;
        }

        public void setAt(int index, Object value) {
            values[index - 1] = value;
        }

        @Override
        public void setNamed(String field, Object value) {
            int index = schema.getIndexNamed(field);
            setAt(index, value);
        }

        @Override
        public DataSetter getSetter() {
            return this;
        }

        @Override
        public NamedData mapToData(Map<? extends String, ?> map) {
            Object[] values = new Object[schema.lastIndex()];
            for (Map.Entry<? extends String, ?> entry: map.entrySet() ) {
                values[schema.getIndexNamed(entry.getKey()) - 1] = entry.getValue();
            }
            return new ArrayData(schema, values);
        }

        @Override
        public NamedData valuesToData(Object... values) {
            return new ArrayData(schema, Arrays.copyOf(values, schema.lastIndex()));
        }

        @Override
        public NamedData toData() {
            Object[] values = this.values;
            this.values = new Object[schema.lastIndex()];
            return new ArrayData(schema, values);
        }
    }
}
