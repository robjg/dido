package dido.data;

import java.util.Objects;

/**
 * Provide indexed access to data.
 *
 * @param <F> The type of field the schema uses. When using data as {@code IndexedData} this type can normally be
 *           the "?" wildcard. It is here to provide a consistent return type for {@link GenericData}.
 */
public interface IndexedData<F> {

    DataSchema<F> getSchema();

    Object getAt(int index);

    default <T> T getAtAs(int index, Class<T> type) {
        //noinspection unchecked
        return (T) getAt(index);
    }

    boolean hasIndex(int index);

    default String getStringAt(int index) {
        return (String) getAt(index);
    }

    default boolean getBooleanAt(int index) {
        return (boolean) getAt(index);
    }

    default byte getByteAt(int index) {
        return (byte) getAt(index);
    }

    default char getCharAt(int index) {
        return (char) getAt(index);
    }

    default short getShortAt(int index) {
        return (short) getAt(index);
    }

    default int getIntAt(int index) {
        return (int) getAt(index);
    }

    default long getLongAt(int index) {
        return (long) getAt(index);
    }

    default float getFloatAt(int index) {
        return (float) getAt(index);
    }

    default double getDoubleAt(int index) {
        return (double) getAt(index);
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
            if (!Objects.equals(data1.getAt(index), data2.getAt(index))) {
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
            Object value = data.getAt(index);
            hash = hash * 31 + (value == null ? 0 :value.hashCode());
        }
        return hash;
    }

    static String toString(IndexedData<?> data) {
        DataSchema<?> schema = data.getSchema();
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
