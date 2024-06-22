package dido.data;

/**
 * Definition for Builders of Data with Named fields. Note this doesn't extend {@link IndexedDataBuilder}
 * because the index to name mapping might not be fully know during the building process.
 *
 *
 */
public interface NamedDataBuilder
        extends DataBuilder<NamedData>{

    NamedDataBuilder with(String field, Object value);

    NamedDataBuilder withBoolean(String field, boolean value);

    NamedDataBuilder withByte(String field, byte value);

    NamedDataBuilder withChar(String field, char value);

    NamedDataBuilder withShort(String field, short value);

    NamedDataBuilder withInt(String field, int value);

    NamedDataBuilder withLong(String field, long value);

    NamedDataBuilder withFloat(String field, float value);

    NamedDataBuilder withDouble(String field, double value);

    NamedDataBuilder withString(String field, String value);

    @Override
    NamedData build();
}
