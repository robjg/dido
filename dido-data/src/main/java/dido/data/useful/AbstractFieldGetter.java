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

    abstract public static class ForBoolean extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getBoolean(data);
        }

        @Override
        abstract public boolean getBoolean(DidoData data);
    }

    abstract public static class ForByte extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getByte(data);
        }

        @Override
        abstract public byte getByte(DidoData data);
    }

    abstract public static class ForChar extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getChar(data);
        }

        @Override
        abstract public char getChar(DidoData data);
    }

    abstract public static class ForShort extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getShort(data);
        }

        @Override
        abstract public short getShort(DidoData data);
    }

    abstract public static class ForInt extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getInt(data);
        }

        @Override
        abstract public int getInt(DidoData data);
    }

    abstract public static class ForLong extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getLong(data);
        }

        @Override
        abstract public long getLong(DidoData data);
    }

    abstract public static class ForFloat extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getFloat(data);
        }

        @Override
        abstract public float getFloat(DidoData data);
    }

    abstract public static class ForDouble extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getDouble(data);
        }

        @Override
        abstract public double getDouble(DidoData data);
    }

    abstract public static class ForString extends AbstractFieldGetter {

        @Override
        public Object get(DidoData data) {
            return getString(data);
        }

        @Override
        abstract public String getString(DidoData data);
    }

}
