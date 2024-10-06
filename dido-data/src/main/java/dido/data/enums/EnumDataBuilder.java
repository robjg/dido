package dido.data.enums;

import dido.data.generic.GenericDataBuilder;

public interface EnumDataBuilder<E extends Enum<E>>
        extends GenericDataBuilder<E> {

    @Override
    EnumDataBuilder<E> with(E field, Object value);

    @Override
    EnumDataBuilder<E> withBoolean(E field, boolean value);

    @Override
    EnumDataBuilder<E> withByte(E field, byte value);

    @Override
    EnumDataBuilder<E> withChar(E field, char value);

    @Override
    EnumDataBuilder<E> withShort(E field, short value);

    @Override
    EnumDataBuilder<E> withInt(E field, int value);

    @Override
    EnumDataBuilder<E> withLong(E field, long value);

    @Override
    EnumDataBuilder<E> withFloat(E field, float value);

    @Override
    EnumDataBuilder<E> withDouble(E field, double value);

    @Override
    EnumDataBuilder<E> withString(E field, String value);

    @Override
    EnumData<E> build();
}
