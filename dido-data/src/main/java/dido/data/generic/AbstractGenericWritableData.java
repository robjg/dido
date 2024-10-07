package dido.data.generic;

import dido.data.useful.AbstractWritableData;

public abstract class AbstractGenericWritableData<F> extends AbstractWritableData
    implements GenericWritableData<F> {

    @Override
    abstract public GenericWriteSchema<F> getSchema();

    @Override
    public void clear(F field) {
        getSchema().getFieldSetter(field).clear(this);
    }

    @Override
    public void set(F field, Object value) {
        getSchema().getFieldSetter(field).set(this, value);
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

}
