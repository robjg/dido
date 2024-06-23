package dido.data;

/**
 * Provides the ability to set data. Not that {@link IndexedSetter} inherits from this and not vice versa.
 * Implementations may provide an Indexed Setter if that is the quickest way to create data
 * (i.e. {@link ArrayData}). If it isn't then they shouldn't (i.e {@link MapData}).
 */
public interface DataSetter {

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
