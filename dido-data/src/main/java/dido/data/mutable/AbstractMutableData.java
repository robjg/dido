package dido.data.mutable;

import dido.data.NoSuchFieldException;
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
    public void clearNamed(String name) {
        int index = getSchema().getIndexNamed(name);
        if (index > 0) {
            clearAt(index);
        }
        else {
            throw new NoSuchFieldException(name, getSchema());
        }
    }

    @Override
    public void setNamed(String name, Object value) {
        int index = getSchema().getIndexNamed(name);
        if (index > 0) {
            setAt(index, value);
        }
        else {
            throw new NoSuchFieldException(name, getSchema());
        }
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
