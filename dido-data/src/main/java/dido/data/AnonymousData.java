package dido.data;

/**
 * Lightweight data structure used internally for things like indices in joins. Equality is based
 * only on value comparison not on schemas.
 */
public interface AnonymousData extends IndexedData {

    @Override
    AnonymousSchema getSchema();

    static boolean equals(AnonymousData data1, AnonymousData data2) {
        return IndexedData.equalsIgnoringSchema(data1, data2);
    }

    static int hashCode(AnonymousData data) {
        AnonymousSchema schema = data.getSchema();
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (!data.hasIndex(index)) {
                continue;
            }
            Object value = data.getAt(index);
            hash = hash * 31 + (value == null ? 0 : value.hashCode());
        }
        return hash;
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
