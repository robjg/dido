package dido.data;

/**
 * Something that is capable of building creating {@link GenericData}. Instances should be reusable,
 * once {@code #build()} has been called, any internal state should be reset so that new data can be built.
 *
 * @param <F> The type of the fields.
 */
public interface IndexedDataBuilder<F> {

    IndexedDataBuilder<F> setAt(int index, Object value);

    IndexedDataBuilder<F> setBooleanAt(int index, boolean value);

    IndexedDataBuilder<F> setByteAt(int index, byte value);

    IndexedDataBuilder<F> setCharAt(int index, char value);

    IndexedDataBuilder<F> setShortAt(int index, short value);

    IndexedDataBuilder<F> setIntAt(int index, int value);

    IndexedDataBuilder<F> setLongAt(int index, long value);

    IndexedDataBuilder<F> setFloatAt(int index, float value);

    IndexedDataBuilder<F> setDoubleAt(int index, double value);

    IndexedDataBuilder<F> setStringAt(int index, String value);

}
