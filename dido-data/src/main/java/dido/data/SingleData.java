package dido.data;

import dido.data.useful.AbstractData;
import dido.data.useful.AbstractDataSchema;
import dido.data.useful.AbstractFieldGetter;

public abstract class SingleData extends AbstractData implements DidoData {

    private final Schema schema;

    protected SingleData(Schema schema) {
        this.schema = schema;
    }

    public static Named named(String name) {

        return new Named(name);
    }

    public static <T> ObjectType<T> type(Class<T> type) {
        return new ObjectType<>(type);
    }

    public static IntType intType() {
        return new IntType();
    }

    public static LongType longType() {
        return new LongType();
    }

    public static DoubleType doubleType() {
        return new DoubleType();
    }

    public static FloatType floatType() {
        return new FloatType();
    }

    public static DidoData of(Object value) {
        return new ObjectType<>(value == null ? void.class : value.getClass()).of(value);
    }

    public static DidoData of(int value) {
        return new IntType().of(value);
    }

    public static DidoData of(long value) {
        return new LongType().of(value);
    }

    public static DidoData of(double value) {
        return new DoubleType().of(value);
    }

    public static DidoData of(float value) {
        return new FloatType().of(value);
    }

    protected static class Schema extends AbstractDataSchema implements ReadStrategy {

        private final SchemaField schemaField;

        private final FieldGetter getter;

        Schema(SchemaField schemaField,
               FieldGetter fieldGetter) {
            this.schemaField = schemaField;
            this.getter = fieldGetter;
        }

        @Override
        public int firstIndex() {
            return 1;
        }

        @Override
        public int nextIndex(int index) {
            return 0;
        }

        @Override
        public int lastIndex() {
            return 1;
        }

        @Override
        public SchemaField getSchemaFieldAt(int index) {
            if (index == 1) {
                return schemaField;
            } else {
                return null;
            }
        }

        @Override
        public SchemaField getSchemaFieldNamed(String name) {
            if (schemaField.getName().equals(name)) {
                return schemaField;
            } else {
                return null;
            }
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            if (index == 1) {
                return getter;
            } else {
                throw new NoSuchFieldException(index, this);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            if (schemaField.getName().equals(name)) {
                return getter;
            } else {
                throw new NoSuchFieldException(name, this);
            }
        }
    }

    @Override
    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        if (index == 1) {
            return schema.getter.get(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public boolean hasIndex(int index) {
        if (index == 1) {
            return schema.getter.has(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public boolean getBooleanAt(int index) {
        if (index == 1) {
            return schema.getter.getBoolean(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public byte getByteAt(int index) {
        if (index == 1) {
            return schema.getter.getByte(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public char getCharAt(int index) {
        if (index == 1) {
            return schema.getter.getChar(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public short getShortAt(int index) {
        if (index == 1) {
            return schema.getter.getShort(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public int getIntAt(int index) {
        if (index == 1) {
            return schema.getter.getInt(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public long getLongAt(int index) {
        if (index == 1) {
            return schema.getter.getLong(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public float getFloatAt(int index) {
        if (index == 1) {
            return schema.getter.getFloat(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public double getDoubleAt(int index) {
        if (index == 1) {
            return schema.getter.getDouble(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    @Override
    public String getStringAt(int index) {
        if (index == 1) {
            return schema.getter.getString(this);
        } else {
            throw new NoSuchFieldException(index, schema);
        }
    }

    public static class Named {

        private final String name;

        public Named(String name) {
            this.name = name;
        }

        public <T> ObjectType<T> type(Class<T> type) {
            return new ObjectType<>(type, name);
        }

        public IntType intType() {
            return new IntType(name);
        }

        public LongType longType() {
            return new LongType(name);
        }

        public DoubleType doubleType() {
            return new DoubleType(name);
        }

        public FloatType floatType() {
            return new FloatType(name);
        }

        public DidoData of(Object value) {
            return new ObjectType<>(value == null ? void.class : value.getClass()).of(value);
        }

        public DidoData of(int value) {
            return intType().of(value);
        }

        public DidoData of(long value) {
            return longType().of(value);
        }

        public DidoData of(double value) {
            return doubleType().of(value);
        }

        public DidoData of(float value) {
            return floatType().of(value);
        }

    }

    public static class ObjectType<T> {

        private final Schema schema;

        ObjectType(Class<?> type) {
            this(type, null);
        }

        ObjectType(Class<?> type,
                   String name) {
            this.schema = new Schema(SchemaField.of(1,
                    name == null ? DataSchema.nameForIndex(1) : name,
                    type),
                    new ObjectGetter());
        }

        public SingleData of(T t) {
            return new ObjectData(schema, t);
        }
    }

    static class ObjectData extends SingleData {

        private final Object value;

        ObjectData(Schema schema, Object value) {
            super(schema);
            this.value = value;
        }
    }

    static class ObjectGetter extends AbstractFieldGetter {

        @Override
        public boolean has(DidoData data) {
            return ((ObjectData) data).value != null;
        }

        @Override
        public Object get(DidoData data) {
            return ((ObjectData) data).value;
        }
    }

    public static class IntType {

        private final Schema schema;

        IntType() {
            this(null);
        }

        IntType(String name) {
            this.schema = new Schema(SchemaField.of(1,
                    name == null ? DataSchema.nameForIndex(1) : name,
                    int.class),
                    new IntGetter());
        }

        public SingleData of(int value) {
            return new IntData(schema, value);
        }
    }

    static class IntData extends SingleData {

        private final int value;

        IntData(Schema schema, int value) {
            super(schema);
            this.value = value;
        }
    }

    static class IntGetter extends AbstractFieldGetter {

        @Override
        public boolean has(DidoData data) {
            return true;
        }

        @Override
        public Object get(DidoData data) {
            return ((IntData) data).value;
        }

        @Override
        public int getInt(DidoData data) {
            return ((IntData) data).value;
        }
    }

    public static class LongType {

        private final Schema schema;

        LongType() {
            this(null);
        }

        LongType(String name) {
            this.schema = new Schema(SchemaField.of(1,
                    name == null ? DataSchema.nameForIndex(1) : name,
                    long.class),
                    new LongGetter());
        }

        public SingleData of(long value) {
            return new LongData(schema, value);
        }
    }

    static class LongData extends SingleData {

        private final long value;

        LongData(Schema schema, long value) {
            super(schema);
            this.value = value;
        }
    }

    static class LongGetter extends AbstractFieldGetter {

        @Override
        public boolean has(DidoData data) {
            return true;
        }

        @Override
        public Object get(DidoData data) {
            return ((LongData) data).value;
        }

        @Override
        public long getLong(DidoData data) {
            return ((LongData) data).value;
        }
    }

    public static class DoubleType {

        private final Schema schema;

        DoubleType() {
            this(null);
        }

        DoubleType(String name) {
            this.schema = new Schema(SchemaField.of(1,
                    name == null ? DataSchema.nameForIndex(1) : name,
                    double.class),
                    new DoubleGetter());
        }

        public SingleData of(double value) {
            return new DoubleData(schema, value);
        }
    }

    static class DoubleData extends SingleData {

        private final double value;

        DoubleData(Schema schema, double value) {
            super(schema);
            this.value = value;
        }
    }

    static class DoubleGetter extends AbstractFieldGetter {

        @Override
        public boolean has(DidoData data) {
            return true;
        }

        @Override
        public Object get(DidoData data) {
            return ((DoubleData) data).value;
        }

        @Override
        public double getDouble(DidoData data) {
            return ((DoubleData) data).value;
        }

        @Override
        public float getFloat(DidoData data) {
            return (float) ((DoubleData) data).value;
        }
    }

    public static class FloatType {

        private final Schema schema;

        FloatType() {
            this(null);
        }

        FloatType(String name) {
            this.schema = new Schema(SchemaField.of(1,
                    name == null ? DataSchema.nameForIndex(1) : name,
                    float.class),
                    new FloatGetter());
        }

        public SingleData of(float value) {
            return new FloatData(schema, value);
        }
    }

    static class FloatData extends SingleData {

        private final float value;

        FloatData(Schema schema, float value) {
            super(schema);
            this.value = value;
        }
    }

    static class FloatGetter extends AbstractFieldGetter {

        @Override
        public boolean has(DidoData data) {
            return true;
        }

        @Override
        public Object get(DidoData data) {
            return ((FloatData) data).value;
        }

        @Override
        public double getDouble(DidoData data) {
            return ((FloatData) data).value;
        }

        @Override
        public float getFloat(DidoData data) {
            return ((FloatData) data).value;
        }
    }


}
