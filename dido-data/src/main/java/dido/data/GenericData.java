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
        //noinspection unchecked
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

    static <F> String toStringFieldsOnly(GenericData<F> data) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<F> it = data.getSchema().getFields().iterator();
        if (!it.hasNext()) {
            sb.append("}");
        }
        for (;;) {
            F field = it.next();
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

    static <F> String toString(GenericData<F> data) {
        DataSchema<F> schema = data.getSchema();
        StringBuilder sb = new StringBuilder(schema.lastIndex() * 16);
        sb.append('{');
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            sb.append('[');
            F field = schema.getFieldAt(index);
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
}
