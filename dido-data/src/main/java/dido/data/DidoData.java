package dido.data;

import dido.data.util.DataBuilder;
import dido.data.util.FieldValuesIn;

import java.util.Iterator;
import java.util.Objects;

/**
 * The basic definition of a Data item within Dido.
 * <p>
 * All instances of {@code DidoData} should be equal if they have the same {@link DataSchema} and their data items
 * are equal.
 * <p>The hashcode of an instance of {@code DidoData} must only be the hash of its data items.
 */
public interface DidoData extends IndexedData {

    @Override
    DataSchema getSchema();

    Object getNamed(String name);

    boolean hasNamed(String name);

    boolean getBooleanNamed(String name);

    char getCharNamed(String name);

    byte getByteNamed(String name);

    short getShortNamed(String name);

    int getIntNamed(String name);

    long getLongNamed(String name);

    float getFloatNamed(String name);

    double getDoubleNamed(String name);

    String getStringNamed(String name);

    static DidoData of(Object... data) {
        return ArrayData.of(data);
    }

    static FieldValuesIn valuesWithSchema(DataSchema schema) {
        return ArrayData.valuesForSchema(schema);
    }

    static DataBuilder builder() {
        return ArrayData.builderNoSchema();
    }

    static DataBuilder builderWithSchema(DataSchema schema) {
        return ArrayData.builderForSchema(schema);
    }

    /**
     * Provide a standard way
     * @param data
     * @return
     */
    static int hashCode(DidoData data) {
        DataSchema schema = data.getSchema();
        int hash = 0;
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (!data.hasIndex(index)) {
                continue;
            }
            Object value = data.getAt(index);
            hash = hash * 31 + (value == null ? 0 :value.hashCode());
        }
        return hash;
    }

    static boolean equals(DidoData data1, DidoData data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }
        DataSchema schema = data1.getSchema();
        if (!schema.equals(data2.getSchema())) {
            return false;
        }
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            if (! Objects.equals(data1.getAt(index), data2.getAt(index))) {
                return false;
            }
        }
        return true;
    }

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
