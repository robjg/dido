package dido.data;

/**
 * Provides the ability to set data. Not that {@link IndexedSetter} inherits from this and not vice versa.
 * Implementations may provide an Indexed Setter if that is the quickest way to create data
 * (i.e. {@link ArrayData}). If it isn't then they shouldn't (i.e {@link MapData}).
 */
public interface DataSetter {

    void clearNamed(String name);
    
    void setNamed(String name, Object value);

    void setBooleanNamed(String name, boolean value);

    void setByteNamed(String name, byte value);

    void setCharNamed(String name, char value);

    void setShortNamed(String name, short value);

    void setIntNamed(String name, int value);

    void setLongNamed(String name, long value);

    void setFloatNamed(String name, float value);

    void setDoubleNamed(String name, double value);

    void setStringNamed(String name, String value);

}
