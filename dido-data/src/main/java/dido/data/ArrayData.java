package dido.data;

import dido.data.generic.GenericData;
import dido.data.useful.*;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 */
public class ArrayData extends AbstractData implements DidoData {

    private final ArrayDataSchema schema;

    private final Object[] data;

    private ArrayData(ArrayDataSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static DidoData of(Object... data) {
        Objects.requireNonNull(data);

        ArrayDataSchemaFactory schemaFactory = new ArrayDataSchemaFactory();
        for (Object datum : data) {
            schemaFactory.addSchemaField(SchemaField.of(0, null,
                    datum == null ? void.class : datum.getClass()));
        }

        return new ArrayData(schemaFactory.toSchema(), data);
    }

    public static SchemaFactory schemaFactory() {
        return new ArrayDataSchemaFactory();
    }

    public static SchemaBuilder schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory());
    }

    public static ArrayDataSchema asArrayDataSchema(DataSchema schema) {

        if (schema instanceof ArrayDataSchema) {
            return (ArrayDataSchema) schema;

        } else {
            return new ArrayDataSchema(schema);
        }
    }

    public static DataBuilder<ArrayData> builderForSchema(DataSchema schema) {

        return builderForSchema(asArrayDataSchema(schema));
    }

    public static DataBuilder<ArrayData> builderForSchema(ArrayDataSchema schema) {

        return new DataBuilder<>(schema.newDataFactory());
    }

    public static BuilderUnknown newBuilderNoSchema() {

        return new BuilderUnknown();
    }

    public static DataFactory<ArrayData> factoryForSchema(DataSchema schema) {
        return new ArrayDataFactory(asArrayDataSchema(schema));
    }

    public static Values<ArrayData> valuesForSchema(DataSchema schema) {

        return Values.withDataFactory(factoryForSchema(schema));
    }

    public static ArrayData copy(DidoData from) {

        return new DataBuilder<>(factoryForSchema(from.getSchema())).copy(from).build();
    }

    @Override
    public ArrayDataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return data[index - 1];
    }

    @Override
    public boolean hasIndex(int index) {
        return data[index - 1] != null;
    }

    public static class BuilderUnknown {

        private final List<SchemaField> schemaFields = new LinkedList<>();

        private final List<Object> values = new LinkedList<>();

        private int lastIndex = 0;


        public BuilderUnknown with(String field, Object value) {
            return setField(field, value, Object.class);
        }

        public BuilderUnknown withBoolean(String field, boolean value) {
            return setField(field, value, boolean.class);
        }

        public BuilderUnknown withByte(String field, byte value) {
            return setField(field, value, byte.class);
        }

        public BuilderUnknown withChar(String field, char value) {
            return setField(field, value, char.class);
        }

        public BuilderUnknown withShort(String field, short value) {
            return setField(field, value, short.class);
        }

        public BuilderUnknown withInt(String field, int value) {
            return setField(field, value, int.class);
        }

        public BuilderUnknown withLong(String field, long value) {
            return setField(field, value, long.class);
        }

        public BuilderUnknown withFloat(String field, float value) {
            return setField(field, value, float.class);
        }

        public BuilderUnknown withDouble(String field, double value) {
            return setField(field, value, double.class);
        }

        public BuilderUnknown withString(String field, String value) {
            return setField(field, value, String.class);
        }

        private BuilderUnknown setField(String field, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(++lastIndex, field, type));
            return this;
        }

        public DidoData build() {
            ArrayDataSchemaFactory schemaBuilder = new ArrayDataSchemaFactory();
            Object[] values = new Object[lastIndex];
            Iterator<Object> valIt = this.values.iterator();
            for (SchemaField schemaField : this.schemaFields) {
                schemaBuilder.addSchemaField(schemaField);
                values[schemaField.getIndex() - 1] = valIt.next();
            }
            return new ArrayData(schemaBuilder.toSchema(), values);
        }
    }

    static class ArrayDataFactory extends AbstractWritableData implements DataFactory<ArrayData> {

        private final ArrayDataSchema schema;

        private Object[] values;

        ArrayDataFactory(ArrayDataSchema schema) {
            this.schema = schema;
            values = new Object[schema.lastIndex()];
        }

        @Override
        public ArrayDataSchema getSchema() {
            return schema;
        }

        @Override
        public Class<ArrayData> getDataType() {
            return ArrayData.class;
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
        public WritableData getWritableData() {
            return this;
        }

        @Override
        public ArrayData toData() {
            Object[] values = this.values;
            this.values = new Object[schema.lastIndex()];
            return new ArrayData(schema, values);
        }
    }

    public static class ArrayDataSchema extends DataSchemaImpl
            implements ReadSchema, WriteSchema {

        ArrayDataSchema(DataSchema from) {
            super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
        }

        ArrayDataSchema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
            super(schemaFields, firstIndex, lastIndex);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            String fieldName = getFieldNameAt(index);
            if (fieldName == null) {
                throw new NoSuchFieldException(index, this);
            }
            String toString = "ArrayDataGetter for [" + index + ":" + fieldName + "]";

            return new AbstractFieldGetter() {
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

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, this);
            }

            return getFieldGetterAt(index);
        }

        public DataFactory<ArrayData> newDataFactory() {
            return new ArrayDataFactory(this);
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            if (!hasIndex(index)) {
                throw new NoSuchFieldException(index, this);
            }
            return new AbstractFieldSetter() {
                @Override
                public void clear(WritableData writable) {
                    ((ArrayDataFactory) writable).values[index - 1] = null;
                }

                @Override
                public void set(WritableData writable, Object value) {
                    ((ArrayDataFactory) writable).values[index - 1] = value;
                }
            };
        }

        @Override
        public FieldSetter getFieldSetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, this);
            }
            return getFieldSetterAt(index);
        }

    }

    static class ArrayDataSchemaFactory extends SchemaFactoryImpl<ArrayDataSchema> {

        protected ArrayDataSchemaFactory() {
        }

        protected ArrayDataSchemaFactory(DataSchema from) {
            super(from);
        }

        @Override
        protected ArrayDataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new ArrayDataSchema(fields, firstIndex, lastIndex);
        }
    }
}
