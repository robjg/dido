package dido.data;

/**
 * Implementations need only provide {@link WritableData#setNamed(String, Object)}.
 */
abstract public class AbstractWritableData implements WritableData {

    @Override
    abstract public WriteSchema getSchema();

    @Override
    public void clearAt(int index) {
        getSchema().getFieldSetterAt(index).clear(this);
    }

    @Override
    public void clearNamed(String name) {
        getSchema().getFieldSetterNamed(name).clear(this);
    }

    @Override
    public void setAt(int index, Object value) {
        getSchema().getFieldSetterAt(index).set(this, value);
    }

    @Override
    public void setNamed(String name, Object value) {
        getSchema().getFieldSetterNamed(name).set(this, value);
    }

    @Override
    public void setBooleanAt(int index, boolean value) {
        setAt(index, value);
    }

    @Override
    public void setByteAt(int index, byte value) {
        setAt(index, value);
    }

    @Override
    public void setShortAt(int index, short value) {
        setAt(index, value);
    }

    @Override
    public void setIntAt(int index, int value) {
        setAt(index, value);
    }

    @Override
    public void setLongAt(int index, long value) {
        setAt(index, value);
    }

    @Override
    public void setFloatAt(int index, float value) {
        setAt(index, value);
    }

    @Override
    public void setDoubleAt(int index, double value) {
        setAt(index, value);
    }

    @Override
    public void setStringAt(int index, String value) {
        setAt(index, value);
    }

    @Override
    public void setBooleanNamed(String name, boolean value) {
        setNamed(name, value);
    }

    @Override
    public void setByteNamed(String name, byte value) {
        setNamed(name, value);
    }

    @Override
    public void setCharNamed(String name, char value) {
        setNamed(name, value);
    }

    @Override
    public void setShortNamed(String name, short value) {
        setNamed(name, value);
    }

    @Override
    public void setIntNamed(String name, int value) {
        setNamed(name, value);
    }

    @Override
    public void setLongNamed(String name, long value) {
        setNamed(name, value);
    }

    @Override
    public void setFloatNamed(String name, float value) {
        setNamed(name, value);
    }

    @Override
    public void setDoubleNamed(String name, double value) {
        setNamed(name, value);
    }

    @Override
    public void setStringNamed(String name, String value) {
        setNamed(name, value);
    }
}
