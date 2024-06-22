package dido.data;

/**
 * Provides the ability to set data. Not that {@link IndexedSetter} inherits from this and not vice versa.
 * Implementations may provide an Indexed Setter if that is the quickest way to create data
 * (i.e. {@link ArrayData}). If it isn't then they shouldn't (i.e {@link MapData}).
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

}
