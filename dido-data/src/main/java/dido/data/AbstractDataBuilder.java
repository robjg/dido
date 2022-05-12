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
    abstract public B set(F field, Object value);

    @Override
    public B setBoolean(F field, boolean value) {
        return set(field, value);
    }

    @Override
    public B setByte(F field, byte value) {
        return set(field, value);
    }

    @Override
    public B setChar(F field, char value) {
        return set(field, value);
    }

    @Override
    public B setShort(F field, short value) {
        return set(field, value);
    }

    @Override
    public B setInt(F field, int value) {
        return set(field, value);
    }

    @Override
    public B setLong(F field, long value) {
        return set(field, value);
    }

    @Override
    public B setFloat(F field, float value) {
        return set(field, value);
    }

    @Override
    public B setDouble(F field, double value) {
        return set(field, value);
    }

    @Override
    public B setString(F field, String value) {
        return set(field, value);
    }
}
