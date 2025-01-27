package dido.data;

import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.util.Iterator;
import java.util.Objects;

/**
 * The basic definition of a Data item within Dido.
 * <p>
 * All instances of {@code DidoData} should be equal if they have the same data items in iteration order
 * regardless of their {@link DataSchema}s.
 * <p>The hashcode of an instance of {@code DidoData} must only be the hash of its data items.
 */
public interface DidoData extends IndexedData {

    @Override
    DataSchema getSchema();

    /**
     * Get the data for the field of the given name. If the name is in the schema then this
     * method will either return the data or null if there is none. If the name is not
     * in the schema then behaviour is undefined.
     *
     * @param name The field name.
     * @return Either some data or null.
     */
    Object getNamed(String name);

    /**
     * Is there data for the field with the given name. If the name is in the schema then this
     * method will either return true or false. If the name is not
     * in the schema then behaviour is undefined.
     *
     * @param name The field name.
     * @return true if there is data, false if there isn't.
     */
    boolean hasNamed(String name);

    /**
     * Get the boolean value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A boolean value.
     */
    boolean getBooleanNamed(String name);

    /**
     * Get the char value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A char value.
     */
    char getCharNamed(String name);

    /**
     * Get the byte value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A byte value.
     */
    byte getByteNamed(String name);

    /**
     * Get the short value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A short value.
     */
    short getShortNamed(String name);

    /**
     * Get the int value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A int value.
     */
    int getIntNamed(String name);

    /**
     * Get the long value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A long value.
     */
    long getLongNamed(String name);

    /**
     * Get the float value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A float value.
     */
    float getFloatNamed(String name);

    /**
     * Get the double value for the field with the given name. If there is no data for the field or the field is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data for the field will be done.
     *
     * @param name The field name.
     * @return A double value.
     */
    double getDoubleNamed(String name);

    /**
     * Get the String value for the field with the given name. If there is no data for the field then null will be returned.
     * If the field is not in the schema then behaviour is undefined. Unless documented, it should be assumed
     * that no conversion of data at the index will be done.
     *
     * @param name the field name
     * @return A boolean value.
     */
    String getStringNamed(String name);

    /**
     * Create {@code DidoData} from the given values.
     *
     * @param data The values.
     * @return The created data.
     */
    static DidoData of(Object... data) {
        return ArrayData.of(data);
    }

    /**
     * Create from field values with the given schema.
     *
     * @param schema The schema. Must not be null.
     * @return A Fluent Builder for specifying the filed values.
     */
    static FieldValuesIn valuesWithSchema(DataSchema schema) {
        return ArrayData.valuesWithSchema(schema);
    }

    /**
     * Provide a builder for creating {@code DidoData}.
     *
     * @return A builder.
     */
    static DataBuilder builder() {
        return ArrayData.builder();
    }

    /**
     * Provide a builder for creating {@code DidoData} that
     * will have the provided schema.
     *
     * @param schema The schema.
     * @return A builder.
     */
    static DataBuilder builderForSchema(DataSchema schema) {
        return ArrayData.builderForSchema(schema);
    }

    /**
     * Copy data from the given data. If the data being copied is
     * immutable then the same data may be returned.
     *
     * @param from The data to be copied.
     * @return The possibly new data.
     */
    static DidoData copy(DidoData from) {

        return ArrayData.copy(from);
    }

    /**
     * Provide a standard way of calculating the hash code.
     *
     * @param data The data.
     * @return The hash code.
     */
    static int hashCode(DidoData data) {
        DataSchema schema = data.getSchema();
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (!data.hasAt(index)) {
                continue;
            }
            Object value = data.getAt(index);
            hash = hash * 31 + (value == null ? 0 :value.hashCode());
        }
        return hash;
    }

    /**
     * Provide a standard way of testing equality. Dido Data equality is based on the iteration
     * order of the values.
     *
     * @param data1 The first data.
     * @param data2 The second data.
     *
     * @return true if they are equal. false otherwise.
     */
    static boolean equals(DidoData data1, DidoData data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }

        DataSchema schema1 = data1.getSchema();
        DataSchema schema2 = data2.getSchema();

        int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
        for ( ; index1 > 0 && index2 > 0; index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {
            if (! Objects.equals(data1.getAt(index1), data2.getAt(index2))) {
                return false;
            }
        }
        return index1 == 0 && index2 == 0;
    }

    /**
     * Test if two data items are equal including their schemas.
     *
     * @param data1 The first data.
     * @param data2 The second data.
     *
     * @return true if they are equal. false otherwise.
     */
    static boolean strictlyEquals(DidoData data1, DidoData data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }
        DataSchema schema = data1.getSchema();
        if (!schema.equals(data2.getSchema())) {
            return false;
        }
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (! Objects.equals(data1.getAt(index), data2.getAt(index))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Provide a standard String representation of {@code DidoData}.
     *
     * @param data The data.
     * @return A string representation.
     */
    static String toString(DidoData data) {
        DataSchema schema = data.getSchema();
        StringBuilder sb = new StringBuilder(schema.lastIndex() * 16);
        sb.append('{');
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            sb.append('[');
            String field = schema.getFieldNameAt(index);
            sb.append(index);
            if (field != null) {
                sb.append(':');
                sb.append(field);
            }
            sb.append("]=");
            sb.append(data.getAt(index));
            if (index != schema.lastIndex()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Provide a standard String representation of {@code DidoData} that only uses the field names,
     * not the indexes.
     *
     * @param data The data.
     * @return A string representation.
     */
    static String toStringFieldsOnly(DidoData data) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<String> it = data.getSchema().getFieldNames().iterator();
        if (!it.hasNext()) {
            return sb.append("}").toString();
        }
        for (;;) {
            String field = it.next();
            sb.append('[');
            sb.append(field);
            sb.append("]=");
            sb.append(data.getNamed(field));
            if (it.hasNext()) {
                sb.append(", ");
            }
            else {
                return sb.append("}").toString();
            }
        }
    }

}
