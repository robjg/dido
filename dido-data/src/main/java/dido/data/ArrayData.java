package dido.data;

import dido.data.generic.GenericData;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 *
 */
public class ArrayData extends AbstractNamedData implements NamedData {

    private final ArrayDataSchema schema;

    private final Object[] data;

    private ArrayData(ArrayDataSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static DidoData of(Object... data) {
        Objects.requireNonNull(data);

        ArrayDataSchema.ArrayDataSchemaFactory schemaFactory = new ArrayDataSchema.ArrayDataSchemaFactory();
        for (int i = 0; i < data.length; ++i) {
            schemaFactory.addSchemaField(SchemaField.of(0, null, Object.class));
        }

        return new ArrayData(schemaFactory.toSchema(), data);
    }

    public static WritableSchemaFactory<ArrayData> schemaFactory() {
        return new ArrayDataSchema.ArrayDataSchemaFactory();
    }

    public static SchemaBuilder<ArrayDataSchema> schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory(), ArrayDataSchema.class);
    }

    public static ArrayDataSchema asArrayDataSchema(DataSchema schema) {

        if (schema instanceof ArrayDataSchema) {
            return (ArrayDataSchema) schema;

        }
        else {
            return new ArrayDataSchema(schema);
        }
    }

    public static Builder builderForSchema(DataSchema schema) {

        return new Builder(asArrayDataSchema(schema));
    }

    public static BuilderUnknown newBuilder() {

        return new BuilderUnknown();
    }

    public static DataBuilders.Values valuesFor(DataSchema schema) {

            return new Builder(asArrayDataSchema(schema)).values();
    }

    public static DataFactory<ArrayData> factoryFor(DataSchema schema) {
        return new ArrayDataFactory(asArrayDataSchema(schema));
    }


    public static Getter getDataGetterAt(int index, DataSchema schema) {
        String toString = "ArrayDataGetter for [" + index + ":" + schema.getFieldNameAt(index) + "]";
        return new AbstractGetter() {
            @Override
            public Object get(DidoData data) {
                return ((ArrayData) data).data[index - 1];
            }
            @Override
            public String toString() {
                return toString;
            }
        };
    }

    public static Getter getDataGetterNamed(String name, DataSchema schema) {
        return getDataGetterAt(schema.getIndexNamed(name), schema);
    }


    @Override
    public ArrayDataSchema getSchema() {
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

    public static class Builder extends DataBuilders.KnownSchema<Builder, ArrayDataSchema> {

        private Object[] values;

        Builder(ArrayDataSchema schema) {
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

        private final List<SchemaField> schemaFields = new LinkedList<>();

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
            schemaFields.add(SchemaField.of(index, DataSchema.nameForIndex(index), type));
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

        public NamedData build() {
            ArrayDataSchema.ArrayDataSchemaFactory schemaBuilder = new ArrayDataSchema.ArrayDataSchemaFactory();
            Object[] values = new Object[lastIndex];
            Iterator<Object> valIt = this.values.iterator();
            for (SchemaField schemaField : this.schemaFields) {
                schemaBuilder.addSchemaField(schemaField);
                values[schemaField.getIndex() - 1] = valIt.next();
            }
            return new ArrayData(schemaBuilder.toSchema(), values);
        }
    }

    static class ArrayDataFactory extends AbstractIndexedSetter implements DataFactory<ArrayData> {

        private final ArrayDataSchema schema;

        private Object[] values;

        ArrayDataFactory(ArrayDataSchema schema) {
            this.schema = schema;
            values = new Object[schema.lastIndex()];
        }

        @Override
        public Class<ArrayData> getDataType() {
            return ArrayData.class;
        }

        @Override
        public Setter getSetterAt(int index) {
            if (!schema.hasIndex(index)) {
                throw new NoSuchFieldException(index, schema);
            }
            return new AbstractSetter() {
                @Override
                public void clear() {
                    clearAt(index);
                }

                @Override
                public void set(Object value) {
                    setAt(index, value);
                }
            };
        }

        @Override
        public Setter getSetterNamed(String name) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, schema);
            }
            return getSetterAt(index);
        }

        @Override
        public void clearAt(int index) {
            values[index - 1] = null;
        }

        @Override
        public void setAt(int index, Object value) {
            values[index - 1] = value;
        }

        @Override
        public void setNamed(String name, Object value) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new IllegalArgumentException(
                        "No field named " + name + ", valid field names: " + schema.getFieldNames());
            }
            setAt(index, value);
        }

        @Override
        public void clearNamed(String name) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new IllegalArgumentException(
                        "No field named " + name + ", valid field names: " + schema.getFieldNames());
            }
            clearAt(index);
        }

        @Override
        public DataSetter getSetter() {
            return this;
        }

        @Override
        public ArrayData valuesToData(Object... values) {
            return new ArrayData(schema, Arrays.copyOf(values, schema.lastIndex()));
        }

        @Override
        public ArrayData toData() {
            Object[] values = this.values;
            this.values = new Object[schema.lastIndex()];
            return new ArrayData(schema, values);
        }
    }

}
