package dido.data;

/**
 * Implementations need only provide {@link DataSetter#set(String, Object)}.
 */
abstract public class AbstractDataSetter implements DataSetter {

    @Override
    abstract public void set(String field, Object value);

    @Override
    public void setBoolean(String field, boolean value) {
        set(field, value);
    }

    @Override
    public void setByte(String field, byte value) {
        set(field, value);
    }

    @Override
    public void setChar(String field, char value) {
        set(field, value);
    }

    @Override
    public void setShort(String field, short value) {
        set(field, value);
    }

    @Override
    public void setInt(String field, int value) {
        set(field, value);
    }

    @Override
    public void setLong(String field, long value) {
        set(field, value);
    }

    @Override
    public void setFloat(String field, float value) {
        set(field, value);
    }

    @Override
    public void setDouble(String field, double value) {
        set(field, value);
    }

    @Override
    public void setString(String field, String value) {
        set(field, value);
    }
}
