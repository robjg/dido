package dido.data;

import java.util.Objects;

/**
 * Lightweight data structure used internally for things like indices in joins. Equality is based
 * only on value comparison not on schemas.
 */
public interface AnonymousData extends IndexedData {

    @Override
    AnonymousSchema getSchema();

    /**
     * Provide a standard way of calculating the hash code.
     *
     * @param data The data.
     * @return The hash code.
     */
    static int hashCode(AnonymousData data) {
        IndexedSchema schema = data.getSchema();
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
     * Test if data is equal. Equality is based on the equality of the values based on iteration order.
     * @param data1 First data
     * @param data2 Second data.
     * @return true if equal, false otherwise.
     */
    static boolean equals(AnonymousData data1, AnonymousData data2) {
        if (data1 == null || data2 == null) {
            return false;
        }

        IndexedSchema schema1 = data1.getSchema();
        IndexedSchema schema2 = data2.getSchema();

        int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
        for ( ; index1 > 0 && index2 > 0; index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {
            if (! Objects.equals(data1.getAt(index1), data2.getAt(index2))) {
                return false;
            }
        }
        return index1 == 0 && index2 == 0;
    }

    static String toString(AnonymousData data) {
        AnonymousSchema schema = data.getSchema();
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
