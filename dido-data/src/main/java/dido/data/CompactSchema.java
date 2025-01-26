package dido.data;

import java.util.Objects;

/**
 * Definition of a schema for {@link CompactData}.
 */
public interface CompactSchema extends IndexedSchema {

    /**
     * Compare two schemas for equality. Because it's forbidden to provide default Object methods in
     * interfaces each these statics are available as a convenience for implementations.
     *
     * @param schema1 The first schema. May be null.
     * @param schema2 The second schema. May be null.
     *
     * @return True if the fields types, the indexes, the types at each index and the
     * fields at each index are the same.
     */
    static boolean equals(CompactSchema schema1, CompactSchema schema2) {
        if (schema1 == schema2) {
            return true;
        }
        if (schema1 == null | schema2 == null) {
            return false;
        }
        if (schema1.lastIndex() != schema2.lastIndex()) {
            return false;
        }

        for (int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
             index1 > 0 || index2 > 0;
             index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {

            if (index1 != index2) {
                return false;
            }
            if (!Objects.equals(schema1.getTypeAt(index1), schema2.getTypeAt(index2))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Provide a hashcode for a schema.
     *
     * @param schema The schema.
     * @return A hash code value.
     */
    static int hashCode(CompactSchema schema) {
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            hash = hash * 31 + schema.getTypeAt(index).hashCode();
        }
        return hash;
    }

    static String toString(CompactSchema schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
            if (i > schema.firstIndex()) {
                sb.append(", ");
            }
            sb.append('[').append(i)
                    .append("]=").append(schema.getTypeAt(i).getName());
        }
        sb.append("}");
        return sb.toString();
    }

}
