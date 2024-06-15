package dido.oddjob.transform;

/**
 * Provides the ability to set data. Intended to sit in front of {@link dido.data.DataBuilders} and
 * not provide the ability to build.
 *
 */
public interface DataSetter {

    void set(String field, Object value);

    void setBoolean(String field, boolean value);

    void setByte(String field, byte value);

    void setChar(String field, char value);

    void setShort(String field, short value);

    void setInt(String field, int value);

    void setLong(String field, long value);

    void setFloat(String field, float value);

    void setDouble(String field, double value);

    void setString(String field, String value);

    void setAt(int index, Object value);

    void setBooleanAt(int index, boolean value);

    void setByteAt(int index, byte value);

    void setShortAt(int index, short value);

    void setIntAt(int index, int value);

    void setLongAt(int index, long value);

    void setFloatAt(int index, float value);

    void setDoubleAt(int index, double value);

    void setStringAt(int index, String value);
}
