package dido.data;

import java.util.Iterator;

/**
 * Provide a generic data structure for moving data in and out.
 *
 * @param <F> The field type. Generally a String or an enum.
 */
public interface GenericData<F> extends IndexedData<F> {

    Object get(F field);

    <T> T getAs(F field, Class<T> type);

    boolean hasField(F field);

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

    static <F> GenericData<F> from(IndexedData<F> indexedData) {

        if (indexedData instanceof GenericData) {
            return (GenericData<F>) indexedData;
        }

        return new AbstractGenericData<>() {
            @Override
            public DataSchema<F> getSchema() {
                return indexedData.getSchema();
            }

            @Override
            public Object getAt(int index) {
                return indexedData.getAt(index);
            }

            @Override
            public boolean hasIndex(int index) {
                return indexedData.hasIndex(index);
            }

            @Override
            public <T> T getAtAs(int index, Class<T> type) {
                return indexedData.getAtAs(index, type);
            }

            @Override
            public boolean getBooleanAt(int index) {
                return indexedData.getBooleanAt(index);
            }

            @Override
            public byte getByteAt(int index) {
                return indexedData.getByteAt(index);
            }

            @Override
            public char getCharAt(int index) {
                return indexedData.getCharAt(index);
            }

            @Override
            public short getShortAt(int index) {
                return indexedData.getShortAt(index);
            }

            @Override
            public int getIntAt(int index) {
                return indexedData.getIntAt(index);
            }

            @Override
            public long getLongAt(int index) {
                return indexedData.getLongAt(index);
            }

            @Override
            public float getFloatAt(int index) {
                return indexedData.getFloatAt(index);
            }

            @Override
            public double getDoubleAt(int index) {
                return indexedData.getDoubleAt(index);
            }

            @Override
            public String getStringAt(int index) {
                return indexedData.getStringAt(index);
            }
        };
    }

    class EmptyData<F> extends AbstractGenericData<F> {

        @Override
        public DataSchema<F> getSchema() {
            return DataSchema.emptySchema();
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
