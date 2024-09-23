package dido.data.generic;

/**
 * Something that is capable of building creating {@link GenericData}. Instances should be reusable,
 * once {@link #build()} has been called, any internal state should be reset so that new data can be built.
 *
 * @param <F> The type of the fields.
 */
public interface GenericDataBuilder<F> {

    GenericDataBuilder<F> with(F field, Object value);

    GenericDataBuilder<F> withBoolean(F field, boolean value);

    GenericDataBuilder<F> withByte(F field, byte value);

    GenericDataBuilder<F> withChar(F field, char value);

    GenericDataBuilder<F> withShort(F field, short value);

    GenericDataBuilder<F> withInt(F field, int value);

    GenericDataBuilder<F> withLong(F field, long value);

    GenericDataBuilder<F> withFloat(F field, float value);

    GenericDataBuilder<F> withDouble(F field, double value);

    GenericDataBuilder<F> withString(F field, String value);

    GenericData<F> build();
}
