package dido.data;

import java.util.Iterator;

/**
 * Provide a generic data structure for moving data in and out.
 *
 * @param <F> The field type. Generally a String or an enum.
 */
public interface GenericData<F> extends IndexedData<F> {

    default Object get(F field) {
        int index = getSchema().getIndex(field);
        if (index > 0) {
            return getAt(index);
        }
        else {
            return null;
        }
    }

    default <T> T getAs(F field, Class<T> type) {
        return (T) get(field);
    }

    default boolean hasField(F field) {
        return get(field) != null;
    }

    default boolean getBoolean(F field) {
        return (boolean) get(field);
    }

    default byte getByte(F field) {
        return (byte) get(field);
    }

    default char getChar(F field) {
        return (char) get(field);
    }

    default short getShort(F field) {
        return (short) get(field);
    }

    default int getInt(F field) {
        return (int) get(field);
    }

    default long getLong(F field) {
        return (long) get(field);
    }

    default float getFloat(F field) {
        return (float) get(field);
    }

    default double getDouble(F field) {
        return (double) get(field);
    }

    default String getString(F field) { return (String) get(field); }

    static <F> String toStringFieldsOnly(String name, GenericData<F> data) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(": {");
        Iterator<F> it = data.getSchema().getFields().iterator();
        if (!it.hasNext()) {
            sb.append("}");
        }
        for (;;) {
            F field = it.next();
            sb.append(field);
            sb.append('=');
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
