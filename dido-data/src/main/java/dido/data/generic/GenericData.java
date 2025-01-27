package dido.data.generic;

import dido.data.DidoData;

import java.util.Iterator;

/**
 * Provide an abstraction of {@link DidoData} providing field access via some other field type.
 *
 * @param <F> The field type.
 */
public interface GenericData<F> extends DidoData {

    GenericReadSchema<F> getSchema();

    Object get(F field);

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

        private final GenericReadSchema<F> schema;

        EmptyData(Class<F> fieldType) {
            this.schema = GenericReadSchema.emptySchema(fieldType);
        }

        @Override
        public GenericReadSchema<F> getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            throw new IndexOutOfBoundsException("No Index " + index);
        }
    }

    static <F> GenericData<F> emptyData(Class<F> fieldType) {
        return new EmptyData<>(fieldType);
    }
}
