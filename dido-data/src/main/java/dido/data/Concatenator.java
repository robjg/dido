package dido.data;

import java.util.*;

/**
 * Provides the ability to Concatenate {@link GenericData}. The data isn't copied but linked in a new master
 * {@code GenericData} object.
 *
 * @param <F>
 */
public class Concatenator<F> {

    private final DataSchema<F> schema;

    private final int[] dataByIndex;

    private final int[] offsets;

    private final Map<F, Integer> dataByField;

    public Concatenator(DataSchema<F> schema,
                        int[] dataByIndex,
                        int[] offsets,
                        Map<F, Integer> dataByField) {
        this.schema = schema;
        this.dataByIndex = dataByIndex;
        this.offsets = offsets;
        this.dataByField = dataByField;
    }

    /**
     * Create a new {@code Concatenator}.
     *
     * @param schemas The schemas of the records that will be concatenated.
     * @param <F> The type of the field.
     *
     * @return A {@code Concatenator}.
     */
    public static <F> Concatenator<F> fromSchemas(DataSchema<F>... schemas) {

        List<F> allFields = new LinkedList<>();

        int firstIndex = 0;
        int lastIndex = 0;
        for (DataSchema<F> schema : schemas) {
            if (firstIndex == 0) {
                firstIndex = schema.firstIndex();
            }
            lastIndex = lastIndex + schema.lastIndex();
        }

        @SuppressWarnings("unchecked")
        OffsetSchema<F>[] schemaByIndex = new OffsetSchema[lastIndex];
        int[] dataByIndex = new int[lastIndex];
        int[] offsets = new int[schemas.length];
        int[] nextIndex = new int[lastIndex];

        Map<F, OffsetSchema<F>> schemaByField = new LinkedHashMap<>();
        Map<F, Integer> dataByField = new HashMap<>();

        int offset = 0;

        lastIndex = 0;

        for (int which = 0; which < schemas.length; ++which) {
            DataSchema<F> schema = schemas[which];

            if (allFields.removeAll(schema.getFields())) {
                throw new IllegalArgumentException("Fields must be unique.");
            }

            allFields.addAll(schema.getFields());

            OffsetSchema<F> offsetSchema = new OffsetSchema<>(schema, offset);

            for (F field : schema.getFields()) {
                schemaByField.put(field, offsetSchema);
                dataByField.put(field, which);
            }

            for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                int index = i + offset;
                if (lastIndex > 0) {
                    nextIndex[lastIndex - 1] = index;
                }
                lastIndex = index;
                schemaByIndex[index - 1] = offsetSchema;
                nextIndex[index - 1] =
                dataByIndex[index - 1] = which;
            }
            offsets[which] = offset;

            offset = offset + schema.lastIndex();
        }
        nextIndex[lastIndex - 1] = 0;

        DataSchema<F> compositeSchema = new CompositeSchema<>(schemaByIndex,
                schemaByField,
                firstIndex,
                lastIndex,
                nextIndex);

        return new Concatenator<>(compositeSchema, dataByIndex, offsets, dataByField);
    }

    public static <F> GenericData<F> of(GenericData<F>... data) {
        return new Factory<F>().concat(data);
    }

    public static <F> Factory<F> factory() {

        return new Factory<>();
    }

    public GenericData<F> concat(GenericData<F>... data) {

        return new ConcatenatedData(data);
    }

    public DataSchema<F> getSchema() {
        return schema;
    }

    public static class Factory<F> {

        private Concatenator<F> last;

        private DataSchema<F>[] previous;

        private GenericData<F> concat(GenericData<F>[] data) {

            boolean recreate = false;
            if (last == null) {
                recreate = true;
                //noinspection unchecked
                previous = new DataSchema[data.length];
                for (int i = 0; i < data.length; ++i) {
                    previous[i] = data[i].getSchema();
                }
            }
            else {
                for (int i = 0; i < data.length; ++i) {
                    if (previous[i] != data[i].getSchema()) {
                        recreate = true;
                        previous[i] = data[i].getSchema();
                    }
                }
            }

            if (recreate) {
                last = fromSchemas(previous);
            }

            return last.concat(data);
        }
    }

    class ConcatenatedData implements GenericData<F> {

        private final GenericData<F>[] data;

        ConcatenatedData(GenericData<F>[] data) {
            this.data = data;
        }

        @Override
        public DataSchema<F> getSchema() {
            return schema;
        }

        @Override
        public Object getObjectAt(int index) {
            int i = dataByIndex[index - 1];
            return data[i].getObjectAt(index - offsets[i]);
        }

        @Override
        public <T> T getObjectAt(int index, Class<T> type) {
            int i = dataByIndex[index - 1];
            return data[i].getObjectAt(index - offsets[i], type);
        }

        @Override
        public boolean hasIndex(int index) {
            int i = dataByIndex[index];
            return data[i].hasIndex(index - offsets[i]);
        }

        @Override
        public String getStringAt(int index) {
            int i = dataByIndex[index];
            return data[i].getStringAt(index - offsets[i]);
        }

        @Override
        public boolean getBooleanAt(int index) {
            int i = dataByIndex[index];
            return data[i].getBooleanAt(index - offsets[i]);
        }

        @Override
        public byte getByteAt(int index) {
            int i = dataByIndex[index];
            return data[i].getByteAt(index - offsets[i]);
        }

        @Override
        public char getCharAt(int index) {
            int i = dataByIndex[index];
            return data[i].getCharAt(index - offsets[i]);
        }

        @Override
        public short getShortAt(int index) {
            int i = dataByIndex[index];
            return data[i].getShortAt(index - offsets[i]);
        }

        @Override
        public int getIntAt(int index) {
            int i = dataByIndex[index];
            return data[i].getIntAt(index - offsets[i]);
        }

        @Override
        public long getLongAt(int index) {
            int i = dataByIndex[index];
            return data[i].getLongAt(index - offsets[i]);
        }

        @Override
        public float getFloatAt(int index) {
            int i = dataByIndex[index];
            return data[i].getFloatAt(index - offsets[i]);
        }

        @Override
        public double getDoubleAt(int index) {
            int i = dataByIndex[index];
            return data[i].getDoubleAt(index - offsets[i]);
        }

        @Override
        public Object getObject(F field) {
            return data[dataByField.get(field)].getObject(field);
        }

        @Override
        public <T> T getObject(F field, Class<T> type) {
            return data[dataByField.get(field)].getObject(field, type);
        }

        @Override
        public boolean hasField(F field) {
            return data[dataByField.get(field)].hasField(field);
        }

        @Override
        public boolean getBoolean(F field) {
            return data[dataByField.get(field)].getBoolean(field);
        }

        @Override
        public byte getByte(F field) {
            return data[dataByField.get(field)].getByte(field);
        }

        @Override
        public char getChar(F field) {
            return data[dataByField.get(field)].getChar(field);
        }

        @Override
        public int getShort(F field) {
            return data[dataByField.get(field)].getShort(field);
        }

        @Override
        public int getInt(F field) {
            return data[dataByField.get(field)].getInt(field);
        }

        @Override
        public long getLong(F field) {
            return data[dataByField.get(field)].getLong(field);
        }

        @Override
        public float getFloat(F field) {
            return data[dataByField.get(field)].getFloat(field);
        }

        @Override
        public double getDouble(F field) {
            return data[dataByField.get(field)].getDouble(field);
        }

        @Override
        public String getString(F field) {
            return data[dataByField.get(field)].getString(field);
        }
    }


    static class OffsetSchema<F>  {

        private final DataSchema<F> originalSchema;

        private final int offset;

        OffsetSchema(DataSchema<F> originalSchema, int offset) {
            this.originalSchema = originalSchema;
            this.offset = offset;
        }

        public F getFieldAt(int index) {
            return originalSchema.getFieldAt(index - offset);
        }

        public Class<?> getTypeAt(int index) {
            return originalSchema.getTypeAt(index - offset);
        }

        public <N> DataSchema<N> getSchemaAt(int index) {
            return originalSchema.getSchemaAt(index - offset);
        }

        public int getIndex(F field) {
            return originalSchema.getIndex(field) + offset;
        }
    }


    static class CompositeSchema<F> implements DataSchema<F> {

        private final OffsetSchema<F>[] schemaByIndex;

        private final Map<F, OffsetSchema<F>> schemaByField;

        private final int firstIndex;

        private final int lastIndex;

        private final int[] nextIndex;

        CompositeSchema(OffsetSchema<F>[] schemaByIndex,
                        Map<F, OffsetSchema<F>> schemaByField,
                        int firstIndex,
                        int lastIndex,
                        int[] nextIndex) {
            this.schemaByIndex = schemaByIndex;
            this.schemaByField = schemaByField;
            this.firstIndex = firstIndex;
            this.lastIndex = lastIndex;
            this.nextIndex = nextIndex;
        }

        @Override
        public F getFieldAt(int index) {
            return schemaByIndex[index - 1].getFieldAt(index);
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return schemaByIndex[index - 1].getTypeAt(index);
        }

        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            return schemaByIndex[index - 1].getSchemaAt(index);
        }

        @Override
        public int getIndex(F field) {
            return schemaByField.get(field).getIndex(field);
        }

        @Override
        public int firstIndex() {
            return firstIndex;
        }

        @Override
        public int nextIndex(int index) {

            return nextIndex[index - 1];
        }

        @Override
        public int lastIndex() {
            return lastIndex;
        }

        @Override
        public Collection<F> getFields() {
            return schemaByField.keySet();
        }
    }

}
