package dido.data;

import java.util.Iterator;

/**
 * The basic definition of a Data item within Dido.
 */
public interface DidoData extends IndexedData {

    Object get(String field);

    <T> T getAs(String field, Class<T> type);

    boolean hasField(String field);

    boolean getBoolean(String field);

    byte getByte(String field);

    char getChar(String field);

    short getShort(String field);

    int getInt(String field);

    long getLong(String field);

    float getFloat(String field);

    double getDouble(String field);

    String getString(String field);

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
            sb.append(data.get(field));
            if (it.hasNext()) {
                sb.append(", ");
            }
            else {
                return sb.append("}").toString();
            }
        }
    }

}
