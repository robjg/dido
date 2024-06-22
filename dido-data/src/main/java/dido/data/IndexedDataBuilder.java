package dido.data;

/**
 * Something that is capable of building creating {@link DidoData}. Instances should be reusable,
 * once {@code #build()} has been called, any internal state should be reset so that new data can be built.
 *
 * @param <D> The Type of Data built
 * @param <B> The builder for fluency.
 */
public interface IndexedDataBuilder<D extends DidoData, B extends IndexedDataBuilder<D, B>> {

    B withAt(int index, Object value);

    B withBooleanAt(int index, boolean value);

    B withByteAt(int index, byte value);

    B withCharAt(int index, char value);

    B withShortAt(int index, short value);

    B withIntAt(int index, int value);

    B withLongAt(int index, long value);

    B withFloatAt(int index, float value);

    B withDoubleAt(int index, double value);

    B withStringAt(int index, String value);

}
