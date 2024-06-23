package dido.data;

/**
 * Implementations need only provide {@link DataSetter#setNamed(String, Object)}.
 */
abstract public class AbstractDataSetter implements DataSetter {

    @Override
    abstract public void setNamed(String field, Object value);

    @Override
    public void setBooleanNamed(String field, boolean value) {
        setNamed(field, value);
    }

    @Override
    public void setByteNamed(String field, byte value) {
        setNamed(field, value);
    }

    @Override
    public void setCharNamed(String field, char value) {
        setNamed(field, value);
    }

    @Override
    public void setShortNamed(String field, short value) {
        setNamed(field, value);
    }

    @Override
    public void setIntNamed(String field, int value) {
        setNamed(field, value);
    }

    @Override
    public void setLongNamed(String field, long value) {
        setNamed(field, value);
    }

    @Override
    public void setFloatNamed(String field, float value) {
        setNamed(field, value);
    }

    @Override
    public void setDoubleNamed(String field, double value) {
        setNamed(field, value);
    }

    @Override
    public void setStringNamed(String field, String value) {
        setNamed(field, value);
    }
}
