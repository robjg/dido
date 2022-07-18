package dido.data;

/**
 * Something that is capable of building creating {@link GenericData}. Instances should be reusable,
 * once {@link #build()} has been called, any internal state should be reset so that new data can be built.
 *
 * @param <F> The type of the fields.
 */
public interface GenericDataBuilder<F> {

    GenericDataBuilder<F> set(F field, Object value);

    GenericDataBuilder<F> setBoolean(F field, boolean value);

    GenericDataBuilder<F> setByte(F field, byte value);

    GenericDataBuilder<F> setChar(F field, char value);

    GenericDataBuilder<F> setShort(F field, short value);

    GenericDataBuilder<F> setInt(F field, int value);

    GenericDataBuilder<F> setLong(F field, long value);

    GenericDataBuilder<F> setFloat(F field, float value);

    GenericDataBuilder<F> setDouble(F field, double value);

    GenericDataBuilder<F> setString(F field, String value);

    GenericData<F> build();
}
