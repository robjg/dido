package dido.data;

/**
 * Data that may be changed.
 *
 * @param <F> The field type.
 */
public interface MutableData<F> extends GenericData<F> {

    void setAt(int index, Object value);

    void  setBooleanAt(int index, boolean value);

    void  setByteAt(int index, byte value);

    void  setCharAt(int index, char value);

    void  setShortAt(int index, short value);

    void  setIntAt(int index, int value);

    void  setLongAt(int index, long value);

    void  setFloatAt(int index, float value);

    void  setDoubleAt(int index, double value);

    void  setStringAt(int index, String value);

    void set(F field, Object value);

    void  setBoolean(F field, boolean value);

    void  setByte(F field, byte value);

    void  setChar(F field, char value);

    void  setShort(F field, short value);

    void  setInt(F field, int value);

    void  setLong(F field, long value);

    void  setFloat(F field, float value);

    void  setDouble(F field, double value);

    void  setString(F field, String value);


}
