package dido.data.generic;

import dido.data.WritableData;

public interface GenericWritableData<F> extends WritableData {

    GenericWriteSchema<F> getSchema();

    void clear(F field);

    void set(F field, Object value);

    void setBoolean(F field, boolean value);

    void setByte(F field, byte value);

    void setShort(F field, short value);

    void setInt(F field, int value);

    void setLong(F field, long value);

    void setFloat(F field, float value);

    void setDouble(F field, double value);

    void setString(F field, String value);

}
