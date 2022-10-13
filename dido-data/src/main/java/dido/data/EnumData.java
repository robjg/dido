package dido.data;

/**
 * Data with an Enum Field.
 *
 * @param <E> The Enum Type.
 */
public interface EnumData<E extends Enum<E>> extends GenericData<E> {

    @Override
    EnumSchema<E> getSchema();

    static <E extends Enum<E>> EnumData<E> fromStringData(GenericData<String> data, Class<E> enumClass) {

        EnumSchema<E> enumSchema = EnumSchema.enumSchemaFrom(data.getSchema(), enumClass);

        return new AbstractEnumData<>() {
            @Override
            public EnumSchema<E> getSchema() {
                return enumSchema;
            }

            @Override
            public Object getAt(int index) {
                return data.getAt(index);
            }

            @Override
            public boolean hasIndex(int index) {
                return data.hasIndex(index);
            }

            @Override
            public String getStringAt(int index) {
                return data.getStringAt(index);
            }

            @Override
            public boolean getBooleanAt(int index) {
                return data.getBooleanAt(index);
            }

            @Override
            public byte getByteAt(int index) {
                return data.getByteAt(index);
            }

            @Override
            public char getCharAt(int index) {
                return data.getCharAt(index);
            }

            @Override
            public short getShortAt(int index) {
                return data.getShortAt(index);
            }

            @Override
            public int getIntAt(int index) {
                return data.getIntAt(index);
            }

            @Override
            public long getLongAt(int index) {
                return data.getLongAt(index);
            }

            @Override
            public float getFloatAt(int index) {
                return data.getFloatAt(index);
            }

            @Override
            public double getDoubleAt(int index) {
                return data.getDoubleAt(index);
            }

            @Override
            public Object get(E field) {
                return data.get(field.toString());
            }

            @Override
            public <T> T getAs(E field, Class<T> type) {
                return data.getAs(field.toString(), type);
            }

            @Override
            public boolean getBoolean(E field) {
                return data.getBoolean(field.toString());
            }

            @Override
            public byte getByte(E field) {
                return data.getByte(field.toString());
            }

            @Override
            public char getChar(E field) {
                return data.getChar(field.toString());
            }

            @Override
            public short getShort(E field) {
                return data.getShort(field.toString());
            }

            @Override
            public int getInt(E field) {
                return data.getInt(field.toString());
            }

            @Override
            public long getLong(E field) {
                return data.getLong(field.toString());
            }

            @Override
            public float getFloat(E field) {
                return data.getFloat(field.toString());
            }

            @Override
            public double getDouble(E field) {
                return data.getDouble(field.toString());
            }

            @Override
            public String getString(E field) {
                return data.getString(field.toString());
            }

            @Override
            public int hashCode() {
                return IndexedData.hashCode(this);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof IndexedData) {
                    return IndexedData.equals(this, (IndexedData<?>) obj);
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return GenericData.toStringFieldsOnly(this);
            }
        };
    }

    static <E extends Enum<E>> GenericData<String> fromEnumData(EnumData<E> data) {

        EnumSchema<E> enumSchema = data.getSchema();

        DataSchema<String> schema = EnumSchema.stringSchemaFrom(enumSchema);

        Class<E> enumClass = enumSchema.getFieldType();

        return new AbstractGenericData<>() {
            @Override
            public DataSchema<String> getSchema() {
                return schema;
            }

            @Override
            public Object getAt(int index) {
                return data.getAt(index);
            }

            @Override
            public boolean hasIndex(int index) {
                return data.hasIndex(index);
            }

            @Override
            public String getStringAt(int index) {
                return data.getStringAt(index);
            }

            @Override
            public boolean getBooleanAt(int index) {
                return data.getBooleanAt(index);
            }

            @Override
            public byte getByteAt(int index) {
                return data.getByteAt(index);
            }

            @Override
            public char getCharAt(int index) {
                return data.getCharAt(index);
            }

            @Override
            public short getShortAt(int index) {
                return data.getShortAt(index);
            }

            @Override
            public int getIntAt(int index) {
                return data.getIntAt(index);
            }

            @Override
            public long getLongAt(int index) {
                return data.getLongAt(index);
            }

            @Override
            public float getFloatAt(int index) {
                return data.getFloatAt(index);
            }

            @Override
            public double getDoubleAt(int index) {
                return data.getDoubleAt(index);
            }

            @Override
            public Object get(String field) {
                return data.get(Enum.valueOf(enumClass, field));
            }

            @Override
            public <T> T getAs(String field, Class<T> type) {
                return data.getAs(Enum.valueOf(enumClass, field), type);
            }

            @Override
            public boolean hasField(String field) {
                return data.hasField(Enum.valueOf(enumClass, field));
            }

            @Override
            public boolean getBoolean(String field) {
                return data.getBoolean(Enum.valueOf(enumClass, field));
            }

            @Override
            public byte getByte(String field) {
                return data.getByte(Enum.valueOf(enumClass, field));
            }

            @Override
            public char getChar(String field) {
                return data.getChar(Enum.valueOf(enumClass, field));
            }

            @Override
            public short getShort(String field) {
                return data.getShort(Enum.valueOf(enumClass, field));
            }

            @Override
            public int getInt(String field) {
                return data.getInt(Enum.valueOf(enumClass, field));
            }

            @Override
            public long getLong(String field) {
                return data.getLong(Enum.valueOf(enumClass, field));
            }

            @Override
            public float getFloat(String field) {
                return data.getFloat(Enum.valueOf(enumClass, field));
            }

            @Override
            public double getDouble(String field) {
                return data.getDouble(Enum.valueOf(enumClass, field));
            }

            @Override
            public String getString(String field) {
                return data.getString(Enum.valueOf(enumClass, field));
            }

            @Override
            public int hashCode() {
                return IndexedData.hashCode(this);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof IndexedData) {
                    return IndexedData.equals(this, (IndexedData<?>) obj);
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return GenericData.toStringFieldsOnly(this);
            }
        };
    }

    interface Builder<E extends Enum<E>> extends GenericDataBuilder<E> {

        Builder<E> set(E field, Object value);

        Builder<E> setBoolean(E field, boolean value);

        Builder<E> setByte(E field, byte value);

        Builder<E> setChar(E field, char value);

        Builder<E> setShort(E field, short value);

        Builder<E> setInt(E field, int value);

        Builder<E> setLong(E field, long value);

        Builder<E> setFloat(E field, float value);

        Builder<E> setDouble(E field, double value);

        Builder<E> setString(E field, String value);

        @Override
        EnumData<E> build();
    }
}