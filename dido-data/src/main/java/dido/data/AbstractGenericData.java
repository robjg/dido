package dido.data;

/**
 * Base class for {@link GenericData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 * @param <F> The field type of the schema.
 */
abstract public class AbstractGenericData<F> extends AbstractData implements GenericData<F> {

    public Object getOf(F field) {
        int index = getSchema().getIndex(field);
        if (index > 0) {
            return getAt(index);
        }
        else {
            return null;
        }
    }

    @Override
    public <T> T getOfAs(F field, Class<T> type) {
        //noinspection unchecked
        return (T) getOf(field);
    }

    @Override
    public boolean hasFieldOf(F field) {
        return getOf(field) != null;
    }

    @Override
    public boolean getBooleanOf(F field) {
        return (boolean) getOf(field);
    }

    @Override
    public byte getByteOf(F field) {
        return (byte) getOf(field);
    }

    @Override
    public char getCharOf(F field) {
        return (char) getOf(field);
    }

    @Override
    public short getShortOf(F field) {
        return (short) getOf(field);
    }

    @Override
    public int getIntOf(F field) {
        return (int) getOf(field);
    }

    @Override
    public long getLongOf(F field) {
        return (long) getOf(field);
    }

    @Override
    public float getFloatOf(F field) {
        return (float) getOf(field);
    }

    @Override
    public double getDoubleOf(F field) {
        return (double) getOf(field);
    }

    @Override
    public String getStringOf(F field) { return (String) getOf(field); }




    @Override
    public String toString() {
        return GenericData.toString(this);
    }

}
