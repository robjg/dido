package dido.data;

import java.util.Objects;

/**
 * Provide indexed access to data. Indexes are always 1 based.
 *
 * @param <F> The type of field the schema uses. When using data as {@code IndexedData} this type can normally be
 *           the "?" wildcard. It is here to provide a consistent return type for {@link GenericData}.
 */
public interface IndexedData<F> {

    /**
     * Get the Data Schema associated with this data.
     *
     * @return A Data Schema, never null.
     */
    DataSchema<F> getSchema();

    /**
     * Get the data at the given index. If the index is in the schema then this
     * method will either return the data or null if there is none. If the index is not
     * int the schema then behaviour is undefined.
     *
     * @param index The index.
     * @return Either some data or null.
     */
    Object getAt(int index);

    <T> T getAtAs(int index, Class<T> type);

    boolean hasIndex(int index);

    boolean getBooleanAt(int index);

    default char getCharAt(int index) {
        return (char) getAt(index);
    }

    byte getByteAt(int index);

    short getShortAt(int index);

    int getIntAt(int index);

    long getLongAt(int index);

    float getFloatAt(int index);

    double getDoubleAt(int index);

    String getStringAt(int index);

    static boolean equals(IndexedData<?> data1, IndexedData<?> data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }
        DataSchema<?> schema = data1.getSchema();
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

    static boolean equalsIgnoringSchema(IndexedData<?> data1, IndexedData<?> data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }
        DataSchema<?> schema = data1.getSchema();
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (! Objects.equals(data1.getAt(index), data2.getAt(index))) {
                return false;
            }
        }
        return true;
    }

    static int hashCode(IndexedData<?> data) {
        DataSchema<?> schema = data.getSchema();
        int hash = schema.hashCode();
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (!data.hasIndex(index)) {
                continue;
            }
            Object value = data.getAt(index);
            hash = hash * 31 + (value == null ? 0 :value.hashCode());
        }
        return hash;
    }

    static String toString(IndexedData<?> data) {
        DataSchema<?> schema = data.getSchema();
        StringBuilder sb = new StringBuilder(schema.lastIndex() * 16);
        sb.append('{');
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            sb.append('[');
            sb.append(index);
            sb.append("]=");
            sb.append(data.getAt(index));
            if (index != schema.lastIndex()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
