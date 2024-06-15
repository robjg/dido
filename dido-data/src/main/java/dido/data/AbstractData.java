package dido.data;

/**
 * Base class for {@link DidoData}. Note that this is preferable to the default methods on
 * the interface because reflection won't recognise the getters as properties, and it's not possible to
 * default the Object methods (toString, hashCode and equals)
 *
 */
abstract public class AbstractData extends AbstractIndexedData implements DidoData {

    @Override
    public Object get(String field) {
        int index = getSchema().getIndexNamed(field);
        if (index > 0) {
            return getAt(index);
        }
        else {
            return null;
        }
    }

    @Override
    public <T> T getAs(String field, Class<T> type) {
        //noinspection unchecked
        return (T) get(field);
    }

    @Override
    public boolean hasField(String field) {
        return get(field) != null;
    }

    @Override
    public boolean getBoolean(String field) {
        return (boolean) get(field);
    }

    @Override
    public byte getByte(String field) {
        return (byte) get(field);
    }

    @Override
    public char getChar(String field) {
        return (char) get(field);
    }

    @Override
    public short getShort(String field) {
        return (short) get(field);
    }

    @Override
    public int getInt(String field) {
        return (int) get(field);
    }

    @Override
    public long getLong(String field) {
        return (long) get(field);
    }

    @Override
    public float getFloat(String field) {
        return (float) get(field);
    }

    @Override
    public double getDouble(String field) {
        return (double) get(field);
    }

    @Override
    public String getString(String field) { return (String) get(field); }

    @Override
    public String toString() {
        return DidoData.toString(this);
    }

}
