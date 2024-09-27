package dido.data;

import dido.data.generic.GenericData;

import java.util.*;

/**
 * {@link GenericData} stored in an Array.
 */
public class ArrayData extends AbstractNamedData implements NamedData {

    private final Schema schema;

    private final Object[] data;

    private ArrayData(Schema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static DidoData of(Object... data) {
        Objects.requireNonNull(data);

        ArrayDataSchemaFactory schemaFactory = new ArrayDataSchemaFactory();
        for (int i = 0; i < data.length; ++i) {
            schemaFactory.addSchemaField(SchemaField.of(0, null, Object.class));
        }

        return new ArrayData(schemaFactory.toSchema(), data);
    }

    public static WritableSchemaFactory<ArrayData> schemaFactory() {
        return new ArrayDataSchemaFactory();
    }

    public static SchemaBuilder<Schema> schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory(), Schema.class);
    }

    public static Schema asArrayDataSchema(DataSchema schema) {

        if (schema instanceof Schema) {
            return (Schema) schema;

        } else {
            return new Schema(schema);
        }
    }

    public static DataBuilder<ArrayData> builderForSchema(DataSchema schema) {

        return builderForSchema(asArrayDataSchema(schema));
    }

    public static DataBuilder<ArrayData> builderForSchema(Schema schema) {

        return new DataBuilder<>(schema);
    }

    public static BuilderUnknown newBuilderNoSchema() {

        return new BuilderUnknown();
    }

    public static Values<ArrayData> valuesForSchema(DataSchema schema) {

        return Values.valuesFor(asArrayDataSchema(schema));
    }

    public static DataFactory<ArrayData> factoryFor(DataSchema schema) {
        return new ArrayDataFactory(asArrayDataSchema(schema));
    }

    @Override
    public Schema getSchema() {
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

        public NamedData build() {
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

        private final Schema schema;

        private Object[] values;

        ArrayDataFactory(Schema schema) {
            this.schema = schema;
            values = new Object[schema.lastIndex()];
        }

        @Override
        public WritableSchema<ArrayData> getSchema() {
            return schema;
        }

        @Override
        public Class<ArrayData> getDataType() {
            return ArrayData.class;
        }

        @Override
        public FieldSetter getSetterAt(int index) {
            if (!schema.hasIndex(index)) {
                throw new NoSuchFieldException(index, schema);
            }
            return new AbstractFieldSetter() {
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
        public FieldSetter getSetterNamed(String name) {
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
        public WritableData getSetter() {
            return this;
        }

        @Override
        public ArrayData toData() {
            Object[] values = this.values;
            this.values = new Object[schema.lastIndex()];
            return new ArrayData(schema, values);
        }
    }

    public static class Schema extends DataSchemaImpl
            implements WritableSchema<ArrayData> {

        Schema(DataSchema from) {
            super(from.getSchemaFields(), from.firstIndex(), from.lastIndex());
        }

        Schema(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
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

        @Override
        public WritableSchemaFactory<ArrayData> newSchemaFactory() {
            return new ArrayDataSchemaFactory();
        }

        @Override
        public DataFactory<ArrayData> newDataFactory() {
            return new ArrayDataFactory(this);
        }
    }

    static class ArrayDataSchemaFactory extends SchemaFactoryImpl<Schema>
            implements WritableSchemaFactory<ArrayData> {

        protected ArrayDataSchemaFactory() {
        }

        protected ArrayDataSchemaFactory(DataSchema from) {
            super(from);
        }

        @Override
        protected Schema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new Schema(fields, firstIndex, lastIndex);
        }
    }
}
