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

    void setNamed(String field, Object value);

    void setBooleanNamed(String field, boolean value);

    void setByteNamed(String field, byte value);

    void setCharNamed(String field, char value);

    void setShortNamed(String field, short value);

    void setIntNamed(String field, int value);

    void setLongNamed(String field, long value);

    void setFloatNamed(String field, float value);

    void setDoubleNamed(String field, double value);

    void setStringNamed(String field, String value);


}
