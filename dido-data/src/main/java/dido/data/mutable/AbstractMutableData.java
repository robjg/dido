package dido.data.mutable;

import dido.data.AbstractData;

public abstract class AbstractMutableData extends AbstractData implements MutableData {

    @Override
    public void setBooleanAt(int index, boolean value) {
        setAt(index, value);
    }

    @Override
    public void setByteAt(int index, byte value) {
        setAt(index, value);
    }

    @Override
    public void setCharAt(int index, char value) {
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
    public void set(String field, Object value) {
        int index = getSchema().getIndexNamed(field);
        if (index > 0) {
            setAt(index, value);
        }
        else {
            throw new IllegalArgumentException("No field " + field);
        }
    }

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
