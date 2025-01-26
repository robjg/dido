package dido.data;

import java.util.function.Function;

/**
 * Lightweight data structure used internally for things like indices in joins. The first value
 * is assumed to be none null and quality is strictly typed so Data of an int
 * will not equal data of an Integer to improve performance.
 */
public interface CompactData extends IndexedData {

    @Override
    CompactSchema getSchema();

    interface Extractor extends Function<DidoData, CompactData> {

        CompactSchema getCompactSchema();
    }

    static String toString(CompactData data) {
        CompactSchema schema = data.getSchema();
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
