package dido.data.mutable;

import dido.data.useful.AbstractData;

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
    public void setNamed(String field, Object value) {
        int index = getSchema().getIndexNamed(field);
        if (index > 0) {
            setAt(index, value);
        }
        else {
            throw new IllegalArgumentException("No field " + field);
        }
    }

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
