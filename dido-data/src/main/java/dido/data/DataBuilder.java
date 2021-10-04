package dido.data;

/**
 * Something that is capable of building creating {@link GenericData}. Instances should be reusable,
 * once {@link #build()} has been called, any internal state should be reset so that new data can be built.
 *
 * @param <F> The type of the fields.
 */
public interface DataBuilder<F> {

    DataBuilder<F> set(F field, Object value);

    DataBuilder<F> setBoolean(F field, boolean value);

    DataBuilder<F> setByte(F field, byte value);

    DataBuilder<F> setChar(F field, char value);

    DataBuilder<F> setShort(F field, short value);

    DataBuilder<F> setInt(F field, int value);

    DataBuilder<F> setLong(F field, long value);

    DataBuilder<F> setFloat(F field, float value);

    DataBuilder<F> setDouble(F field, double value);

    DataBuilder<F> setString(F field, String value);

    GenericData<F> build();
}
