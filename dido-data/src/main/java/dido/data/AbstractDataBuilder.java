package dido.data;

/**
 * Base class for {@link DataBuilder}s that convert primitives into Objects.
 *
 * @param <F> The field type.
 * @param <B> The builder type.
 */
abstract public class AbstractDataBuilder<F, B extends AbstractDataBuilder<F, B>>
        implements DataBuilder<F> {

    @Override
    abstract public GenericData<F> build();

    @Override
    abstract public B setObject(F field, Object value);

    @Override
    public B setBoolean(F field, boolean value) {
        return setObject(field, value);
    }

    @Override
    public B setByte(F field, byte value) {
        return setObject(field, value);
    }

    @Override
    public DataBuilder<F> setChar(F field, char value) {
        return setObject(field, value);
    }

    @Override
    public B setShort(F field, short value) {
        return setObject(field, value);
    }

    @Override
    public B setInt(F field, int value) {
        return setObject(field, value);
    }

    @Override
    public B setLong(F field, long value) {
        return setObject(field, value);
    }

    @Override
    public B setFloat(F field, float value) {
        return setObject(field, value);
    }

    @Override
    public B setDouble(F field, double value) {
        return setObject(field, value);
    }

    @Override
    public B setString(F field, String value) {
        return setObject(field, value);
    }
}
