package dido.data.mutable;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.immutable.ArrayDataDataFactoryProvider;
import dido.data.schema.DataSchemaImpl;
import dido.data.schema.SchemaBuilder;
import dido.data.schema.SchemaFactoryImpl;
import dido.data.useful.AbstractFieldGetter;
import dido.data.useful.AbstractFieldSetter;
import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * An implementation of {@link MutableData} backed by an Array.
 */
public class MutableArrayData extends AbstractMutableData implements MutableData {

    private final Object[] data;

    private final ArrayDataSchema schema;

    private MutableArrayData(ArrayDataSchema schema) {
        this.schema = schema;
        this.data = new Object[schema.lastIndex()];
    }

    private MutableArrayData(ArrayDataSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static MutableArrayData of(Object... data) {
        Object[] copy = new Object[Objects.requireNonNull(data).length];

        ArrayDataSchemaFactory schemaFactory = new ArrayDataSchemaFactory();
        for (int i = 0; i < data.length; i++) {
            Object datum = data[i];
            schemaFactory.addSchemaField(SchemaField.of(i + 1, null,
                    datum == null ? void.class : datum.getClass()));
            copy[i] = datum;
        }

        return new MutableArrayData(schemaFactory.toSchema(), copy);
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
        return new MutableArrayData(asArrayDataSchema(schema)).new ArrayDataFactory();
    }

    public static FromValues withSchema(DataSchema schema) {

        return FieldValuesIn.withDataFactory(factoryForSchema(schema));
    }

    public static MutableArrayData copy(DidoData from) {

        if (from instanceof MutableArrayData mutableArrayData) {
            Object[] copy = Arrays.copyOf(mutableArrayData.data, mutableArrayData.data.length);
            return new MutableArrayData((ArrayDataSchema) from.getSchema(), copy);
        }
        else {
            return (MutableArrayData) withSchema(from.getSchema()).copy(from);
        }
    }

    @Override
    public ArrayDataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        try {
            return data[index - 1];
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public void clearAt(int index) {
        setAt(index, null);
    }

    @Override
    public void clearNamed(String name) {
        int index = getSchema().getIndexNamed(name);
        if (index > 0) {
            clearAt(index);
        }
        else {
            throw new NoSuchFieldException(name, getSchema());
        }
    }

    @Override
    public void setAt(int index, Object value) {
        try {
            data[index -1] = value;
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoSuchFieldException(index, schema);
        }
    }

    class ArrayDataFactory implements DataFactory {

        @Override
        public ArrayDataSchema getSchema() {

            return MutableArrayData.this.getSchema();
        }

        @Override
        public WritableData getWritableData() {
            return MutableArrayData.this;
        }

        @Override
        public MutableArrayData toData() {
            return MutableArrayData.this;
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
                throw new dido.data.NoSuchFieldException(index, this);
            }
            String toString = "ArrayDataGetter for [" + index + ":" + fieldName + "]";

            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return ((MutableArrayData) data).data[index - 1];
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
                throw new dido.data.NoSuchFieldException(name, this);
            }

            return getFieldGetterAt(index);
        }

        public DataFactory dataFactory() {
            return new MutableArrayData(this).new ArrayDataFactory();
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            if (!hasIndex(index)) {
                throw new dido.data.NoSuchFieldException(index, this);
            }
            return new AbstractFieldSetter() {
                @Override
                public void clear(WritableData writable) {
                    ((MutableArrayData) writable).data[index - 1] = null;
                }

                @Override
                public void set(WritableData writable, Object value) {
                    ((MutableArrayData) writable).data[index - 1] = value;
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

        ArrayDataSchemaFactory() {
        }

        ArrayDataSchemaFactory(DataSchema from) {
            super(from);
        }

        @Override
        protected ArrayDataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new ArrayDataSchema(fields, firstIndex, lastIndex);
        }
    }
}
