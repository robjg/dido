package dido.data;

/**
 * Index Setters for {@link DidoData}. Instances should be reusable,
 * once {@link DataFactory#toData()} has been called, any internal state
 * should be reset so that new data can be built.
 *
 */
public interface IndexedSetter extends DataSetter {

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
