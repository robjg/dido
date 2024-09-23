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

        private final Setter[] setters;

        private Object[] values;

        private int[] ints;

        private double[] doubles;

        private Factory(Schema schema) {
            this.schema = schema;
            this.values = new Object[schema.values];
            this.ints = new int[schema.ints];
            this.doubles = new double[schema.doubles];
            this.setters = new Setter[schema.getters.length];
            for (int i = 0; i < setters.length; ++i) {
                Getter getter = schema.getters[i];
                if (getter instanceof IntGetter) {
                    setters[i] = new IntSetter(((IntGetter) getter).index);
                }
                else if (getter instanceof DoubleGetter) {
                    setters[i] = new DoubleSetter(((DoubleGetter) getter).index);
                }
                else {
                    setters[i] = new ValueSetter(((ValueGetter) getter).index);
                }
            }
        }

        @Override
        public Class<NonBoxedData> getDataType() {
            return NonBoxedData.class;
        }

        @Override
        public Setter getSetterAt(int index) {
            try {
                Setter setter = setters[index - 1];
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
        public Setter getSetterNamed(String name) {
            int index = schema.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, schema);
            }
            return getSetterAt(index);
        }

        @Override
        public DataSetter getSetter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public NonBoxedData valuesToData(Object... values) {
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

        class IntSetter extends AbstractSetter {

            private final int index;

            IntSetter(int index) {
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

        class DoubleSetter extends AbstractSetter {

            private final int index;

            DoubleSetter(int index) {
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

        class ValueSetter extends AbstractSetter {

            private final int index;

            ValueSetter(int index) {
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

    static class IntGetter extends AbstractGetter {

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

    static class DoubleGetter extends AbstractGetter {

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

    static class ValueGetter extends AbstractGetter {

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

        private final Getter[] getters;

        private final int values;

        private final int ints;

        private final int doubles;

        private Schema(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
            super(fields, firstIndex, lastIndex);

            getters = new Getter[lastIndex];

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
        public Getter getDataGetterAt(int index) {
            try {
                Getter getter = getters[index - 1];
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
        public Getter getDataGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getDataGetterAt(index);
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
