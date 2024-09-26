package dido.data;

import dido.data.generic.GenericData;
import dido.data.generic.GenericDataBuilder;

/**
 * Data with an Enum Field.
 *
 * @param <E> The Enum Type.
 */
public interface EnumData<E extends Enum<E>> extends GenericData<E> {

    @Override
    EnumSchema<E> getSchema();

    static <E extends Enum<E>> EnumData<E> fromDidoData(DidoData data, Class<E> enumClass) {

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
                return data.getNamed(field.toString());
            }

            @Override
            public boolean getBoolean(E field) {
                return data.getBooleanNamed(field.toString());
            }

            @Override
            public byte getByte(E field) {
                return data.getByteNamed(field.toString());
            }

            @Override
            public char getChar(E field) {
                return data.getCharNamed(field.toString());
            }

            @Override
            public short getShort(E field) {
                return data.getShortNamed(field.toString());
            }

            @Override
            public int getInt(E field) {
                return data.getIntNamed(field.toString());
            }

            @Override
            public long getLong(E field) {
                return data.getLongNamed(field.toString());
            }

            @Override
            public float getFloat(E field) {
                return data.getFloatNamed(field.toString());
            }

            @Override
            public double getDouble(E field) {
                return data.getDoubleNamed(field.toString());
            }

            @Override
            public String getString(E field) {
                return data.getStringNamed(field.toString());
            }

            @Override
            public String toString() {
                return GenericData.toStringFieldsOnly(this);
            }
        };
    }


    interface Builder<E extends Enum<E>>
            extends GenericDataBuilder<E> {

        @Override
        Builder<E> with(E field, Object value);

        @Override
        Builder<E> withBoolean(E field, boolean value);

        @Override
        Builder<E> withByte(E field, byte value);

        @Override
        Builder<E> withChar(E field, char value);

        @Override
        Builder<E> withShort(E field, short value);

        @Override
        Builder<E> withInt(E field, int value);

        @Override
        Builder<E> withLong(E field, long value);

        @Override
        Builder<E> withFloat(E field, float value);

        @Override
        Builder<E> withDouble(E field, double value);

        @Override
        Builder<E> withString(E field, String value);

        @Override
        EnumData<E> build();
    }
}