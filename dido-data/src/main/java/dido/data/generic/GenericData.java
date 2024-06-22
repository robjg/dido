package dido.data.generic;

import dido.data.DidoData;

import java.util.Iterator;

/**
 * Provide a generic data structure for moving data in and out.
 *
 * @param <F> The field type. Generally a String or an enum.
 */
public interface GenericData<F> extends DidoData {

    GenericDataSchema<F> getSchema();


    Object get(F field);

    <T> T getAs(F field, Class<T> type);

    boolean has(F field);

    boolean getBoolean(F field);

    byte getByte(F field);

    char getChar(F field);

    short getShort(F field);

    int getInt(F field);

    long getLong(F field);

    float getFloat(F field);

    double getDouble(F field);

    String getString(F field);

    static <F> String toStringFieldsOnly(GenericData<F> data) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<F> it = data.getSchema().getFields().iterator();
        if (!it.hasNext()) {
            return sb.append("}").toString();
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
        GenericDataSchema<F> schema = data.getSchema();
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

    class EmptyData<F> extends AbstractGenericData<F> {

        @Override
        public GenericDataSchema<F> getSchema() {
            return GenericDataSchema.emptySchema();
        }

        @Override
        public Object getAt(int index) {
            throw new IndexOutOfBoundsException("No Index " + index);
        }
    }

    static <F> GenericData<F> emptyData() {
        return new EmptyData<>();
    }
}
