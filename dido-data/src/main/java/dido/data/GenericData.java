package dido.data;

/**
 * Provide a generic data structure for moving data in and out.
 *
 * @param <F> The field type. Generally a String or an enum.
 */
public interface GenericData<F> extends IndexedData<F> {

    default Object getObject(F field) {
        return getObject(field, Object.class);
    }

    default <T> T getObject(F field, Class<T> type) {
        int index = getSchema().getIndex(field);
        if (index > 0) {
            return getObjectAt(index, type);
        }
        else {
            return null;
        }
    }

    default boolean hasField(F field) {
        int index = getSchema().getIndex(field);
        if (index > 0) {
            return hasIndex(index);
        }
        else {
            return false;
        }
    }

    default boolean getBoolean(F field) {
        return getBooleanAt(getSchema().getIndex(field));
    }

    default byte getByte(F field) {
        return getByteAt(getSchema().getIndex(field));
    }

    default char getChar(F field) {
        return getCharAt(getSchema().getIndex(field));
    }

    default int getShort(F field) {
        return getShortAt(getSchema().getIndex(field));
    }

    default int getInt(F field) {
        return getIntAt(getSchema().getIndex(field));
    }

    default long getLong(F field) {
        return getLongAt(getSchema().getIndex(field));
    }

    default float getFloat(F field) {
        return getFloatAt(getSchema().getIndex(field));
    }

    default double getDouble(F field) {
        return getDoubleAt(getSchema().getIndex(field));
    }

    default String getString(F field) { return getStringAt(getSchema().getIndex(field)); }
}
