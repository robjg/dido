package dido.data;

/**
 * Provides the ability to set data. Instances should be reusable,
 * once {@link DataFactory#toData()} has been called, any internal state
 * should be reset so that new data can be built.
 *
 */
public interface WritableData {

    WriteSchema getSchema();

    void clearAt(int index);

    void setAt(int index, Object value);

    void setBooleanAt(int index, boolean value);

    void setByteAt(int index, byte value);

    void setShortAt(int index, short value);

    void setIntAt(int index, int value);

    void setLongAt(int index, long value);

    void setFloatAt(int index, float value);

    void setDoubleAt(int index, double value);

    void setStringAt(int index, String value);

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
