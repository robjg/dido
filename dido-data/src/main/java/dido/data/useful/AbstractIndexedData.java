package dido.data.useful;

import dido.data.IndexedData;

/**
 * Base class for {@link IndexedData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 */
abstract public class AbstractIndexedData implements IndexedData {

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
        throw new UnsupportedOperationException("Subclasses must overwrite this.");
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Subclasses must overwrite this.");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Subclasses must overwrite this.");
    }
}
