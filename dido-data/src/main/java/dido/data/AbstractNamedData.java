package dido.data;

/**
 * Base class providing default implementations for {@link NamedData}. Implementations
 * need only implement {@link IndexedData#getAt(int)} and {@link IndexedData#getSchema()}.
 */
public abstract class AbstractNamedData extends AbstractData implements NamedData {

    @Override
    public Object get(String name) {
        return getNamed(name);
    }

    @Override
    public <T> T getAs(String name, Class<T> type) {
        //noinspection unchecked
        return (T) get(name);
    }

    @Override
    public boolean has(String name) {
        return hasNamed(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return getBooleanNamed(name);
    }

    @Override
    public byte getByte(String name) {
        return getByteNamed(name);
    }

    @Override
    public char getChar(String name) {
        return getCharNamed(name);
    }

    @Override
    public short getShort(String name) {
        return getShortNamed(name);
    }

    @Override
    public int getInt(String name) {
        return getIntNamed(name);
    }

    @Override
    public long getLong(String name) {
        return getLongNamed(name);
    }

    @Override
    public float getFloat(String name) {
        return getFloatNamed(name);
    }

    @Override
    public double getDouble(String name) {
        return getDoubleNamed(name);
    }

    @Override
    public String getString(String name) { return getStringNamed(name); }
}
