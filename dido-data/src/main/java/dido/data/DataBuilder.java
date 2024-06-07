package dido.data;

/**
 * Something that is capable of building creating {@link DidoData}. Instances should be reusable,
 * once {@link #build()} has been called, any internal state should be reset so that new data can be built.
 *
 */
public interface DataBuilder {

    DataBuilder set(String field, Object value);

    DataBuilder setBoolean(String field, boolean value);

    DataBuilder setByte(String field, byte value);

    DataBuilder setChar(String field, char value);

    DataBuilder setShort(String field, short value);

    DataBuilder setInt(String field, int value);

    DataBuilder setLong(String field, long value);

    DataBuilder setFloat(String field, float value);

    DataBuilder setDouble(String field, double value);

    DataBuilder setString(String field, String value);

    DidoData build();
}
