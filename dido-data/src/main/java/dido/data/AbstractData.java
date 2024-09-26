package dido.data;

/**
 * Base class providing default implementations for {@link DidoData}. Implementations
 * need only implement {@link IndexedData#getAt(int)} and {@link DidoData#getSchema()}.
 *
 */
abstract public class AbstractData extends AbstractIndexedData implements DidoData {

    protected int indexOfFieldNamed(String name) {
        int index = getSchema().getIndexNamed(name);
        if (index == 0) {
            throw new NoSuchFieldException(name, getSchema());
        }
        return index;
    }

    @Override
    public Object getNamed(String name) {
        return getAt(indexOfFieldNamed(name));
    }

    @Override
    public boolean hasNamed(String name) {
        return getNamed(name) != null;
    }

    @Override
    public boolean getBooleanNamed(String name) {
        return getBooleanAt(indexOfFieldNamed(name));
    }

    @Override
    public byte getByteNamed(String name) {
        return getByteAt(indexOfFieldNamed(name));
    }

    @Override
    public char getCharNamed(String name) {
        return getCharAt(indexOfFieldNamed(name));
    }

    @Override
    public short getShortNamed(String name) {
        return getShortAt(indexOfFieldNamed(name));
    }

    @Override
    public int getIntNamed(String name) {
        return getIntAt(indexOfFieldNamed(name));
    }

    @Override
    public long getLongNamed(String name) {
        return getLongAt(indexOfFieldNamed(name));
    }

    @Override
    public float getFloatNamed(String name) {
        return getFloatAt(indexOfFieldNamed(name));
    }

    @Override
    public double getDoubleNamed(String name) {
        return getDoubleAt(indexOfFieldNamed(name));
    }

    @Override
    public String getStringNamed(String name) { return getStringAt(indexOfFieldNamed(name)); }

    @Override
    public int hashCode() {
        return DidoData.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DidoData) {
            return DidoData.equals(this, (DidoData) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return DidoData.toString(this);
    }

}
