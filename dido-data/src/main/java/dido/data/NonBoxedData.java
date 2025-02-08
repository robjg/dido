package dido.data;

import dido.data.useful.*;
import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Implementation of {@link DidoData} that stores primitives in there none boxed form. At the moment this is
 * only happening for Integers and Doubles. Experiments indicate about a 10% performance improvement
 * over {@link ArrayData}.
 */
public class NonBoxedData extends AbstractData {

    private final NonBoxedDataSchema schema;

    private final Object[] values;

    private final int[] ints;

    private final double[] doubles;

    private NonBoxedData(Factory factory) {
        this.schema = factory.schema;
        this.values = factory.values;
        this.ints = factory.ints;
        this.doubles = factory.doubles;
    }

    public static NonBoxedData of(Object... data) {

        SchemaFactory schemaFactory = schemaFactory();
        for (int i = 0; i < data.length; i++) {
            Object datum = data[i];
            schemaFactory.addSchemaField(SchemaField.of(i + 1, null,
                    datum == null ? void.class : datum.getClass()));
        }

        return (NonBoxedData) valuesWithSchema(schemaFactory.toSchema()).of(data);
    }

    public static SchemaFactory schemaFactory() {
        return new NonBoxedDataSchemaFactory();
    }

    public static SchemaBuilder schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory());
    }

    public static NonBoxedDataSchema asNonBoxedDataSchema(DataSchema schema) {

        if (schema instanceof NonBoxedDataSchema) {
            return (NonBoxedDataSchema) schema;
        } else {
            return new NonBoxedDataSchema(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex());
        }
    }

    public static DataFactory factoryForSchema(DataSchema schema) {
        return new Factory(asNonBoxedDataSchema(schema));
    }

    public static DataBuilder builderForSchema(DataSchema schema) {

        return DataBuilder.forFactory(factoryForSchema(schema));
    }

    public static DataBuilder builder() {

        return DataBuilder.forProvider(new NonBoxedDataFactoryProvider());
    }

    public static FieldValuesIn valuesWithSchema(DataSchema schema) {

        return FieldValuesIn.withDataFactory(factoryForSchema(schema));
    }

    public static NonBoxedData copy(DidoData from) {

        return (NonBoxedData) valuesWithSchema(from.getSchema()).copy(from);
    }

    @Override
    public NonBoxedDataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return schema.getters[index - 1].get(this);
    }

    @Override
    public int getIntAt(int index) {
        return schema.getters[index - 1].getInt(this);
    }

    @Override
    public double getDoubleAt(int index) {
        return schema.getters[index - 1].getDouble(this);
    }


    static class IntGetter extends AbstractFieldGetter {

        private final int index;

        IntGetter(int index) {
            this.index = index;
        }

        @Override
        public Object get(DidoData data) {
            return getInt(data);
        }

        @Override
        public int getInt(DidoData data) {
            return ((NonBoxedData) data).ints[index];
        }
    }

    static class DoubleGetter extends AbstractFieldGetter {

        private final int index;

        DoubleGetter(int index) {
            this.index = index;
        }

        @Override
        public Object get(DidoData data) {
            return getDouble(data);
        }

        @Override
        public double getDouble(DidoData data) {
            return ((NonBoxedData) data).doubles[index];
        }
    }

    static class ValueGetter extends AbstractFieldGetter {

        private final int index;

        ValueGetter(int index) {
            this.index = index;
        }

        @Override
        public Object get(DidoData data) {
            return ((NonBoxedData) data).values[index];
        }
    }

    static class Factory implements DataFactory, WritableData {

        private final NonBoxedDataSchema schema;

        private Object[] values;

        private int[] ints;

        private double[] doubles;

        private Factory(NonBoxedDataSchema schema) {
            this.schema = schema;
            this.values = new Object[schema.values];
            this.ints = new int[schema.ints];
            this.doubles = new double[schema.doubles];
        }

        @Override
        public NonBoxedDataSchema getSchema() {
            return schema;
        }

        @Override
        public WritableData getWritableData() {
            return this;
        }

        @Override
        public NonBoxedData toData() {
            NonBoxedData nonBoxedData = new NonBoxedData(this);
            this.values = new Object[schema.values];
            this.ints = new int[schema.ints];
            this.doubles = new double[schema.doubles];
            return nonBoxedData;
        }

        @Override
        public void clearAt(int index) {
            getSetterWithCheck(index).clear(this);
        }

        @Override
        public void setAt(int index, Object value) {
            getSetterWithCheck(index).set(this, value);
        }

        @Override
        public void setBooleanAt(int index, boolean value) {
            getSetterWithCheck(index).setBoolean(this, value);
        }

        @Override
        public void setByteAt(int index, byte value) {
            getSetterWithCheck(index).setByte(this, value);
        }

        @Override
        public void setCharAt(int index, char value) {
            getSetterWithCheck(index).setChar(this, value);
        }

        @Override
        public void setShortAt(int index, short value) {
            getSetterWithCheck(index).setShort(this, value);
        }

        @Override
        public void setIntAt(int index, int value) {
            getSetterWithCheck(index).setInt(this, value);
        }

        @Override
        public void setLongAt(int index, long value) {
            getSetterWithCheck(index).setLong(this, value);
        }

        @Override
        public void setFloatAt(int index, float value) {
            getSetterWithCheck(index).setFloat(this, value);
        }

        @Override
        public void setDoubleAt(int index, double value) {
            getSetterWithCheck(index).setDouble(this, value);
       }

        @Override
        public void setStringAt(int index, String value) {
            getSetterWithCheck(index).setString(this, value);
        }

        @Override
        public void clearNamed(String name) {
            getSetterWithCheck(name).clear(this);
        }

        @Override
        public void setNamed(String name, Object value) {
            getSetterWithCheck(name).set(this, value);
        }

        @Override
        public void setBooleanNamed(String name, boolean value) {
            getSetterWithCheck(name).setBoolean(this, value);
        }

        @Override
        public void setByteNamed(String name, byte value) {
            getSetterWithCheck(name).setByte(this, value);
        }

        @Override
        public void setCharNamed(String name, char value) {
            getSetterWithCheck(name).setChar(this, value);
        }

        @Override
        public void setShortNamed(String name, short value) {
            getSetterWithCheck(name).setShort(this, value);
        }

        @Override
        public void setIntNamed(String name, int value) {
            getSetterWithCheck(name).setInt(this, value);
        }

        @Override
        public void setLongNamed(String name, long value) {
            getSetterWithCheck(name).setLong(this, value);
        }

        @Override
        public void setFloatNamed(String name, float value) {
            getSetterWithCheck(name).setFloat(this, value);
        }

        @Override
        public void setDoubleNamed(String name, double value) {
            getSetterWithCheck(name).setDouble(this, value);
        }

        @Override
        public void setStringNamed(String name, String value) {
            getSetterWithCheck(name).setString(this, value);
        }

        FieldSetter getSetterWithCheck(int index) {
            try {
                return schema.setters[index - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, schema);
            }
        }

        FieldSetter getSetterWithCheck(String name) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, schema);
            }
            return schema.setters[index];
        }
    }

    static class IntFieldSetter extends AbstractFieldSetter {

        private final int index;

        IntFieldSetter(int index) {
            this.index = index;
        }

        @Override
        public void clear(WritableData writeable) {
            setInt(writeable, 0);
        }

        @Override
        public void set(WritableData writeable, Object value) {
            setInt(writeable, (int) value);
        }

        @Override
        public void setInt(WritableData writeable, int value) {
            ((Factory) writeable).ints[index] = value;
        }
    }

    static class DoubleFieldSetter extends AbstractFieldSetter {

        private final int index;

        DoubleFieldSetter(int index) {
            this.index = index;
        }

        @Override
        public void clear(WritableData writeable) {
            setDouble(writeable, 0.0);
        }

        @Override
        public void set(WritableData writeable, Object value) {
            setDouble(writeable, (double) value);
        }

        @Override
        public void setDouble(WritableData writeable, double value) {
            ((Factory) writeable).doubles[index] = value;
        }
    }

    static class ValueFieldSetter extends AbstractFieldSetter {

        private final int index;

        ValueFieldSetter(int index) {
            this.index = index;
        }

        @Override
        public void clear(WritableData writeable) {
            ((Factory) writeable).values[index] = null;
        }

        @Override
        public void set(WritableData writeable, Object value) {
            ((Factory) writeable).values[index] = value;
        }
    }

    public static class NonBoxedDataSchema extends DataSchemaImpl
            implements ReadSchema, WriteSchema {

        private final FieldGetter[] getters;

        private final FieldSetter[] setters;

        private final int values;

        private final int ints;

        private final int doubles;

        private NonBoxedDataSchema(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            super(fields, firstIndex, lastIndex);

            this.getters = new FieldGetter[lastIndex];
            this.setters = new FieldSetter[lastIndex];

            int values = 0;
            int ints = 0;
            int doubles = 0;

            for (SchemaField schemaField : fields) {
                Type type = schemaField.getType();
                int index = schemaField.getIndex();
                int arrayIndex = index - 1;
                if (type == int.class) {
                    getters[arrayIndex] = new IntGetter(ints);
                    setters[arrayIndex] = new IntFieldSetter(ints++);
                } else if (type == double.class) {
                    getters[arrayIndex] = new DoubleGetter(doubles);
                    setters[arrayIndex] = new DoubleFieldSetter(doubles++);
                } else {
                    getters[arrayIndex] = new ValueGetter(values);
                    setters[arrayIndex] = new ValueFieldSetter(values++);
                }
            }

            this.values = values;
            this.ints = ints;
            this.doubles = doubles;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                FieldGetter getter = getters[index - 1];
                if (getter == null) {
                    throw new NoSuchFieldException(index, NonBoxedDataSchema.this);
                }
                return getter;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, NonBoxedDataSchema.this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, NonBoxedDataSchema.this);
            }
            return getFieldGetterAt(index);
        }

        @Override
        public FieldSetter getFieldSetterAt(int index) {
            try {
                FieldSetter setter = setters[index - 1];
                if (setter == null) {
                    throw new NoSuchFieldException(index, this);
                }
                return setter;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, this);
            }
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

    static class NonBoxedDataSchemaFactory extends SchemaFactoryImpl<NonBoxedDataSchema>
            implements SchemaFactory {

        @Override
        protected NonBoxedDataSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new NonBoxedDataSchema(fields, firstIndex, lastIndex);
        }
    }

}
