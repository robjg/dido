package dido.data;

import java.util.Collection;

public class NonBoxedData extends AbstractData {

    private final Schema schema;

    private final Object[] values;

    private final int[] ints;

    private final double[] doubles;

    private NonBoxedData(Factory factory) {
        this.schema = factory.schema;
        this.values = factory.values;
        this.ints = factory.ints;
        this.doubles = factory.doubles;
    }

    public static WritableSchemaFactory<NonBoxedData> schemaFactory() {
        return new SchemaFactory();
    }

    public static SchemaBuilder<Schema> schemaBuilder() {
        return SchemaBuilder.builderFor(schemaFactory(), Schema.class);
    }

    public static Schema asNonBoxedDataSchema(DataSchema schema) {

        if (schema instanceof Schema) {
            return (Schema) schema;
        }
        else {
            return new Schema(schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex());
        }
    }

    public static DataBuilder<NonBoxedData> builderForSchema(DataSchema schema) {

        return new DataBuilder<>(asNonBoxedDataSchema(schema));
    }

    public static DataBuilder<NonBoxedData> builderForSchema(Schema schema) {

        return new DataBuilder<>(schema);
    }

    @Override
    public WritableSchema<NonBoxedData> getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return schema.getters[index -1].get(this);
    }

    @Override
    public int getIntAt(int index) {
        return schema.getters[index -1].getInt(this);
    }

    @Override
    public double getDoubleAt(int index) {
        return schema.getters[index -1].getDouble(this);
    }

    static class Factory implements DataFactory<NonBoxedData> {

        private final Schema schema;

        private final FieldSetter[] setters;

        private Object[] values;

        private int[] ints;

        private double[] doubles;

        private Factory(Schema schema) {
            this.schema = schema;
            this.values = new Object[schema.values];
            this.ints = new int[schema.ints];
            this.doubles = new double[schema.doubles];
            this.setters = new FieldSetter[schema.getters.length];
            for (int i = 0; i < setters.length; ++i) {
                FieldGetter getter = schema.getters[i];
                if (getter instanceof IntGetter) {
                    setters[i] = new IntFieldSetter(((IntGetter) getter).index);
                }
                else if (getter instanceof DoubleGetter) {
                    setters[i] = new DoubleFieldSetter(((DoubleGetter) getter).index);
                }
                else {
                    setters[i] = new ValueFieldSetter(((ValueGetter) getter).index);
                }
            }
        }

        @Override
        public WritableSchema<NonBoxedData> getSchema() {
            return schema;
        }

        @Override
        public Class<NonBoxedData> getDataType() {
            return NonBoxedData.class;
        }

        @Override
        public FieldSetter getSetterAt(int index) {
            try {
                FieldSetter setter = setters[index - 1];
                if (setter == null) {
                    throw new NoSuchFieldException(index, schema);
                }
                return setter;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, schema);
            }
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
        public WritableData getSetter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public NonBoxedData toData() {
            NonBoxedData nonBoxedData = new NonBoxedData(this);
            this.values = new Object[schema.values];
            this.ints = new int[schema.ints];
            this.doubles = new double[schema.doubles];
            return nonBoxedData;
        }

        class IntFieldSetter extends AbstractFieldSetter {

            private final int index;

            IntFieldSetter(int index) {
                this.index = index;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Can not clear a primitive field");
            }

            @Override
            public void set(Object value) {
                setInt((int) value);
            }

            @Override
            public void setInt(int value) {
                ints[index] = value;
            }
        }

        class DoubleFieldSetter extends AbstractFieldSetter {

            private final int index;

            DoubleFieldSetter(int index) {
                this.index = index;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Can not clear a primitive field");
            }

            @Override
            public void set(Object value) {
                setDouble((double) value);
            }

            @Override
            public void setDouble(double value) {
                doubles[index] = value;
            }
        }

        class ValueFieldSetter extends AbstractFieldSetter {

            private final int index;

            ValueFieldSetter(int index) {
                this.index = index;
            }

            @Override
            public void clear() {
                values[index] = null;
            }

            @Override
            public void set(Object value) {
                values[index] = value;
            }
        }
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

    public static class Schema extends DataSchemaImpl
            implements WritableSchema<NonBoxedData> {

        private final FieldGetter[] getters;

        private final int values;

        private final int ints;

        private final int doubles;

        private Schema(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            super(fields, firstIndex, lastIndex);

            getters = new FieldGetter[lastIndex];

            int values = 0;
            int ints = 0;
            int doubles = 0;

            for (SchemaField schemaField : fields) {
                Class<?> type =  schemaField.getType();
                int index = schemaField.getIndex();
                if (type == int.class) {
                    getters[index - 1] = new IntGetter(ints++);
                }
                else if (type == double.class) {
                    getters[index - 1] = new DoubleGetter(doubles++);
                }
                else {
                    getters[index - 1] = new ValueGetter(values++);
                }
            }

            this.values = values;
            this.ints = ints;
            this.doubles = doubles;
        }

        @Override
        public WritableSchemaFactory<NonBoxedData> newSchemaFactory() {
            return new SchemaFactory();
        }

        @Override
        public DataFactory<NonBoxedData> newDataFactory() {
            return new Factory(this);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            try {
                FieldGetter getter = getters[index - 1];
                if (getter == null) {
                    throw new NoSuchFieldException(index, Schema.this);
                }
                return getter;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchFieldException(index, Schema.this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getFieldGetterAt(index);
        }
    }

    static class SchemaFactory extends SchemaFactoryImpl<Schema>
            implements WritableSchemaFactory<NonBoxedData> {

        @Override
        protected Schema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            return new Schema(fields, firstIndex, lastIndex);
        }
    }

}
