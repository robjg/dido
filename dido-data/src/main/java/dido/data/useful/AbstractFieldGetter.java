package dido.data.useful;

import dido.data.DidoData;
import dido.data.FieldGetter;

import java.util.Objects;

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
        return ((Number) get(data)).byteValue();
    }

    @Override
    public char getChar(DidoData data) {
        return (char) get(data);
    }

    @Override
    public short getShort(DidoData data) {
        return ((Number) get(data)).shortValue();
    }

    @Override
    public int getInt(DidoData data) {
        return ((Number) get(data)).intValue();
    }

    @Override
    public long getLong(DidoData data) {
        return ((Number) get(data)).longValue();
    }

    @Override
    public float getFloat(DidoData data) {
        return ((Number) get(data)).floatValue();
    }

    @Override
    public double getDouble(DidoData data) {
        return ((Number) get(data)).doubleValue();
    }

    @Override
    public String getString(DidoData data) {
        return Objects.toString(get(data));
    }

}
