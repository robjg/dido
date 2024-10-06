package dido.data;

/**
 * Provides the ability to set data.
 */
public interface FieldSetter {

    void clear(WritableData data);
    
    void set(WritableData data, Object value);

    void setBoolean(WritableData data, boolean value);

    void setByte(WritableData data, byte value);

    void setChar(WritableData data, char value);

    void setShort(WritableData data, short value);

    void setInt(WritableData data, int value);

    void setLong(WritableData data, long value);

    void setFloat(WritableData data, float value);

    void setDouble(WritableData data, double value);

    void setString(WritableData data, String value);

}
