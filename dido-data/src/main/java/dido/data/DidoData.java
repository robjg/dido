package dido.data;

import java.util.Iterator;

/**
 * The basic definition of a Data item within Dido.
 */
public interface DidoData extends IndexedData {

    Object getNamed(String fieldName);

    <T> T getNamedAs(String fieldName, Class<T> type);

    boolean hasNamed(String fieldName);

    boolean getBooleanNamed(String fieldName);

    char getCharNamed(String fieldName);

    byte getByteNamed(String fieldName);

    short getShortNamed(String fieldName);

    int getIntNamed(String fieldName);

    long getLongNamed(String fieldName);

    float getFloatNamed(String fieldName);

    double getDoubleNamed(String fieldName);

    String getStringNamed(String fieldName);

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
