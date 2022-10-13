package dido.data;

/**
 * Base class for {@link IndexedData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 * @param <F> The field type of the schema.
 */
abstract public class AbstractIndexedData<F> implements IndexedData<F> {

    private volatile int hash = 0;

    @Override
    public <T> T getAtAs(int index, Class<T> type) {
        //noinspection unchecked
        return (T) getAt(index);
    }

    @Override
    public boolean hasIndex(int index) {
        return getAt(index) != null;
    }

    @Override
    public boolean getBooleanAt(int index) {
        return (boolean) getAt(index);
    }

    @Override
    public byte getByteAt(int index) {
        return (byte) getAt(index);
    }

    @Override
    public char getCharAt(int index) {
        return (char) getAt(index);
    }

    @Override
    public short getShortAt(int index) {
        return (short) getAt(index);
    }

    @Override
    public int getIntAt(int index) {
        return (int) getAt(index);
    }

    @Override
    public long getLongAt(int index) {
        return (long) getAt(index);
    }

    @Override
    public float getFloatAt(int index) {
        return (float) getAt(index);
    }

    @Override
    public double getDoubleAt(int index) {
        return (double) getAt(index);
    }

    @Override
    public String getStringAt(int index) {
        return (String) getAt(index);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = IndexedData.hashCode(this);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return IndexedData.toString(this);
    }
}
