package dido.data.enums;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.generic.GenericData;

import java.util.Objects;

/**
 * Data with an Enum Field.
 *
 * @param <E> The Enum Type.
 */
public interface EnumData<E extends Enum<E>> extends GenericData<E> {

    @Override
    EnumReadSchema<E> getSchema();

    static <E extends Enum<E>> EnumData<E> fromDidoData(DidoData data, Class<E> enumClass) {

        DataSchema schema = data.getSchema();

        ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);

        EnumSchema<E> enumSchema = EnumSchema.enumSchemaFrom(schema, enumClass);

        class Schema extends EnumSchemaDelegate<E> implements EnumReadSchema<E> {

            Schema() {
                super(enumSchema);
            }

            @Override
            public FieldGetter getFieldGetterAt(int index) {
                return readStrategy.getFieldGetterAt(index);
            }

            @Override
            public FieldGetter getFieldGetterNamed(String name) {
                return readStrategy.getFieldGetterNamed(name);
            }

            @Override
            public FieldGetter getFieldGetter(E field) {
                int index = enumSchema.getIndexOf(Objects.requireNonNull(field));
                if (index == 0) {
                    throw new NoSuchFieldException(field.toString(), enumSchema);
                }
                else {
                    return getFieldGetterAt(index);
                }
            }
        }

        return new AbstractEnumData<>() {
            @Override
            public EnumReadSchema<E> getSchema() {
                return new Schema() ;
            }

            @Override
            public Object getAt(int index) {
                return data.getAt(index);
            }

            @Override
            public boolean hasAt(int index) {
                return data.hasAt(index);
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


}