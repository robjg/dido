package dido.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provide indexed access to data.
 *
 * @param <F> The type of field the schema uses. When using data as {@code IndexedData} this type can normally be
 *           the "?" wildcard. It is here to provide a consistent return type for {@link GenericData}.
 */
public interface IndexedData<F> {

    DataSchema<F> getSchema();

    <T> T getObjectAt(int index, Class<T> type);

    boolean hasIndex(int index);

    default Object getObjectAt(int index) {
        return getObjectAt(index, Object.class);
    }

    default String getStringAt(int index) {
        return getObjectAt(index, String.class);
    }

    default boolean getBooleanAt(int index) {
        return getObjectAt(index, Boolean.class);
    }

    default byte getByteAt(int index) {
        return getObjectAt(index, Byte.class);
    }

    default char getCharAt(int index) {
        return getObjectAt(index, Character.class);
    }

    default short getShortAt(int index) {
        return getObjectAt(index, Short.class);
    }

    default int getIntAt(int index) {
        return getObjectAt(index, Integer.class);
    }

    default long getLongAt(int index) {
        return (long) getObjectAt(index);
    }

    default float getFloatAt(int index) {
        return getObjectAt(index, Float.class);
    }

    default double getDoubleAt(int index) {
        return (double) getObjectAt(index, Object.class);
    }

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
            if (data1.hasIndex(index) != data1.hasIndex(index)) {
                return false;
            }
            if (!data1.hasIndex(index)) {
                continue;
            }
            Class<?> type = schema.getTypeAt(index);
            if (type.isPrimitive()) {
                if (int.class == type) {
                    if (data1.getIntAt(index) != data2.getIntAt(index)) {
                        return false;
                    }
                }
                else if (double.class == type) {
                    if (data1.getDoubleAt(index) != data2.getDoubleAt(index)) {
                        return false;
                    }
                }
                else if (long.class == type) {
                    if (data1.getLongAt(index) != data2.getLongAt(index)) {
                        return false;
                    }
                }
                else if (boolean.class == type) {
                    if (data1.getBooleanAt(index) != data2.getBooleanAt(index)) {
                        return false;
                    }
                }
                else if (byte.class == type) {
                    if (data1.getByteAt(index) != data2.getByteAt(index)) {
                        return false;
                    }
                }
                else if (char.class == type) {
                    if (data1.getCharAt(index) != data2.getCharAt(index)) {
                        return false;
                    }
                }
                else if (short.class == type) {
                    if (data1.getShortAt(index) != data2.getShortAt(index)) {
                        return false;
                    }
                }
                else if (float.class == type) {
                    if (data1.getFloatAt(index) != data2.getFloatAt(index)) {
                        return false;
                    }
                }
            }
            if (!Objects.equals(data1.getObjectAt(index), data2.getObjectAt(index))) {
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
            Object value = data.getObjectAt(index);
            hash = hash * 31 + (value == null ? 0 :value.hashCode());
        }
        return hash;
    }

    static String toString(IndexedData<?> data) {
        DataSchema<?> schema = data.getSchema();
        List<Object> list = new ArrayList<>(schema.lastIndex());
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (!data.hasIndex(index)) {
                continue;
            }
            list.add(data.getObjectAt(index));
        }
        return list.toString();
    }
}
