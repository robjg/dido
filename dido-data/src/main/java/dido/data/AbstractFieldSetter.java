package dido.data;

/**
 * Provides the ability to set data.
 */
public abstract class AbstractFieldSetter implements FieldSetter {

    @Override
    abstract public void clear(WritableData writable);

    @Override
    abstract public void set(WritableData writable, Object value);

    @Override
    public void setBoolean(WritableData writable, boolean value) {
        set(writable, value);
    }

    @Override
    public void setByte(WritableData writable, byte value) {
        set(writable, value);
    }

    @Override
    public void setChar(WritableData writable, char value) {
        set(writable, value);
    }

    @Override
    public void setShort(WritableData writable, short value) {
        set(writable, value);
    }

    @Override
    public void setInt(WritableData writable, int value) {
        set(writable, value);
    }

    @Override
    public void setLong(WritableData writable, long value) {
        set(writable, value);
    }

    @Override
    public void setFloat(WritableData writable, float value) {
        set(writable, value);
    }

    @Override
    public void setDouble(WritableData writable, double value) {
        set(writable, value);
    }

    @Override
    public void setString(WritableData writable, String value) {
        set(writable, value);
    }

}
