package dido.data.generic;

import dido.data.useful.AbstractData;

/**
 * Base class for {@link GenericData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 * @param <F> The field type of the schema.
 */
abstract public class AbstractGenericData<F> extends AbstractData implements GenericData<F> {

    public Object get(F field) {
        int index = getSchema().getIndexOf(field);
        if (index > 0) {
            return getAt(index);
        } else {
            return null;
        }
    }

    @Override
    public boolean has(F field) {
        return get(field) != null;
    }

    @Override
    public boolean getBoolean(F field) {
        return (boolean) get(field);
    }

    @Override
    public byte getByte(F field) {
        return (byte) get(field);
    }

    @Override
    public char getChar(F field) {
        return (char) get(field);
    }

    @Override
    public short getShort(F field) {
        return (short) get(field);
    }

    @Override
    public int getInt(F field) {
        return (int) get(field);
    }

    @Override
    public long getLong(F field) {
        return (long) get(field);
    }

    @Override
    public float getFloat(F field) {
        return (float) get(field);
    }

    @Override
    public double getDouble(F field) {
        return (double) get(field);
    }

    @Override
    public String getString(F field) {
        return (String) get(field);
    }

    @Override
    public String toString() {
        return GenericData.toString(this);
    }

}
