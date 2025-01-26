package dido.data;

import dido.data.generic.GenericData;
import dido.data.useful.*;
import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.util.Collection;
import java.util.Objects;

/**
 * {@link GenericData} stored in an Array.
 */
public final class ArrayData extends AbstractData implements DidoData {

    private final ArrayDataSchema schema;

    private final Object[] data;

    private ArrayData(ArrayDataSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static ArrayData of(Object... data) {
        Object[] copy = new Object[Objects.requireNonNull(data).length];

        ArrayDataSchemaFactory schemaFactory = new ArrayDataSchemaFactory();
        for (int i = 0; i < data.length; i++) {
            Object datum = data[i];
            schemaFactory.addSchemaField(SchemaField.of(i + 1, null,
                    datum == null ? void.class : datum.getClass()));
            copy[i] = datum;
        }

        return new ArrayData(schemaFactory.toSchema(), copy);
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

    public static DataBuilder builderForSchema(DataSchema schema) {

        return DataBuilder.forFactory(factoryForSchema(schema));
    }

    public static DataBuilder builder() {

        return DataBuilder.forProvider(new ArrayDataDataFactoryProvider());
    }

    public static DataFactory factoryForSchema(DataSchema schema) {
        return new ArrayDataFactory(asArrayDataSchema(schema));
    }

    public static FieldValuesIn valuesWithSchema(DataSchema schema) {

        return FieldValuesIn.withDataFactory(factoryForSchema(schema));
    }

    public static ArrayData copy(DidoData from) {

        if (from instanceof ArrayData) {
            return (ArrayData) from;
        }
        else {
            return (ArrayData) valuesWithSchema(from.getSchema()).copy(from);
        }
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
    public boolean hasAt(int index) {
        return data[index - 1] != null;
    }

    static class ArrayDataFactory extends AbstractWritableData implements DataFactory {

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

        public DataFactory newDataFactory() {
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
