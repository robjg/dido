package dido.data.useful;

import dido.data.DidoData;
import dido.data.FieldGetter;

/**
 * Base class for {@link FieldGetter}s that don't mind autoboxing. Implementations must override
 * {@link FieldGetter#get(DidoData)}.
 *
 */
abstract public class AbstractFieldGetter implements FieldGetter {

    @Override
    abstract public Object get(DidoData data);

    @Override
    public boolean has(DidoData data) {
        return get(data) != null;
    }

    @Override
    public boolean getBoolean(DidoData data) {
        return (boolean) get(data);
    }

    @Override
    public byte getByte(DidoData data) {
        return (byte) get(data);
    }

    @Override
    public char getChar(DidoData data) {
        return (char) get(data);
    }

    @Override
    public short getShort(DidoData data) {
        return (short) get(data);
    }

    @Override
    public int getInt(DidoData data) {
        return (int) get(data);
    }

    @Override
    public long getLong(DidoData data) {
        return (long) get(data);
    }

    @Override
    public float getFloat(DidoData data) {
        return (float) get(data);
    }

    @Override
    public double getDouble(DidoData data) {
        return (double) get(data);
    }

    @Override
    public String getString(DidoData data) {
        return (String) get(data);
    }

}
