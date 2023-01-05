package dido.data;

public abstract class AbstractMutableData<F> extends AbstractGenericData<F> implements MutableData<F> {

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
    public void set(F field, Object value) {
        int index = getSchema().getIndex(field);
        if (index > 0) {
            setAt(index, value);
        }
        else {
            throw new IllegalArgumentException("No field " + field);
        }
    }

    @Override
    public void setBoolean(F field, boolean value) {
        set(field, value);
    }

    @Override
    public void setByte(F field, byte value) {
        set(field, value);
    }

    @Override
    public void setChar(F field, char value) {
        set(field, value);
    }

    @Override
    public void setShort(F field, short value) {
        set(field, value);
    }

    @Override
    public void setInt(F field, int value) {
        set(field, value);
    }

    @Override
    public void setLong(F field, long value) {
        set(field, value);
    }

    @Override
    public void setFloat(F field, float value) {
        set(field, value);
    }

    @Override
    public void setDouble(F field, double value) {
        set(field, value);
    }

    @Override
    public void setString(F field, String value) {
        set(field, value);
    }

    @Override
    public String toString() {
        return GenericData.toString(this);
    }

}
