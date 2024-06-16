package dido.data.mutable;

import dido.data.DidoData;

/**
 * Data that may be changed.
 *
 */
public interface MutableData extends DidoData {

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

    void set(String field, Object value);

    void  setBoolean(String field, boolean value);

    void  setByte(String field, byte value);

    void  setChar(String field, char value);

    void  setShort(String field, short value);

    void  setInt(String field, int value);

    void  setLong(String field, long value);

    void  setFloat(String field, float value);

    void  setDouble(String field, double value);

    void  setString(String field, String value);


}
