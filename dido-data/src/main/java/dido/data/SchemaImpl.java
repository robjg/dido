package dido.data;

import java.util.*;

public class SchemaImpl<F> extends AbstractDataSchema<F> {

    private final Map<F, Integer> fieldToIndex;

    private final Map<F, SchemaField<F>> fieldToSchemaField;

    private final F[] indexToField;

    private final int[] nextIndex;

    private final SchemaField<F>[] indexToSchemaField;

    private final int firstIndex;

    private final int lastIndex;

    private volatile int hashCode = -1;

    @SuppressWarnings("unchecked")
    private SchemaImpl(Iterable<SchemaField<F>> schemaFields, int firstIndex, int lastIndex) {

        this.fieldToSchemaField = new LinkedHashMap<>();
        this.fieldToIndex = new HashMap<>();

        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;

        int offset = firstIndex - 1;
        Object[] indexToField =  new Object[lastIndex - offset];
        int[] nextIndex = new int[lastIndex - offset];
        SchemaField<F>[] indexToSchemaField = new SchemaField[lastIndex - offset];

        int last = 0;
        for (SchemaField<F> meta : schemaFields) {
            int index = meta.getIndex();

            indexToSchemaField[index - firstIndex] = meta;
            indexToField[index - firstIndex] = meta.getField();

            F field = meta.getField();
            if (field != null) {
                fieldToIndex.put(field, index);
                fieldToSchemaField.put(field, meta);
            }

            if (last != 0) {
                nextIndex[last - firstIndex] = index;
            }
            last = index;
        }

        //noinspection unchecked
        this.indexToField = (F[]) indexToField;
        this.nextIndex = nextIndex;
        this.indexToSchemaField = indexToSchemaField;
    }

    public static <F> DataSchema<F> fromFields(SchemaField<F>... schemaFields) {

        return new SchemaImpl<>(() -> Arrays.stream(schemaFields).iterator(),
                schemaFields[0].getIndex(), schemaFields[schemaFields.length - 1].getIndex());
    }


    public static <F> DataSchema<F> fromFields(Iterable<SchemaField<F>> schemaFields, int firstIndex, int lastIndex) {

        return new SchemaImpl<>(schemaFields, firstIndex, lastIndex);
    }

    @Override
    public int firstIndex() {
        return firstIndex;
    }

    @Override
    public int lastIndex() {
        return lastIndex;
    }

    @Override
    public int nextIndex(int index) {
        return this.nextIndex[index - firstIndex];
    }

    @Override
    public SchemaField<F> getSchemaFieldAt(int index) {
        return indexToSchemaField[index - firstIndex];
    }

    @Override
    public Class<?> getTypeAt(int index) {
        return indexToSchemaField[index - firstIndex].getType();
    }

    @Override
    public F getFieldAt(int index) {
        return indexToField[index - firstIndex];
    }

    @Override
    public <N> DataSchema<N> getSchemaAt(int index) {
        SchemaField<F> schemaField = indexToSchemaField[index - firstIndex];
        if (schemaField == null) {
            return null;
        } else {
            return schemaField.getNestedSchema();
        }
    }

    @Override
    public Collection<F> getFields() {
        return fieldToSchemaField.keySet();
    }

    @Override
    public int getIndex(F field) {
        Integer index = fieldToIndex.get(field);
        return index == null ? 0 : index;
    }

    @Override
    public SchemaField<F> getSchemaField(F field) {
        return fieldToSchemaField.get(field);
    }

    @Override
    public <N> DataSchema<N> getSchema(F field) {
        SchemaField<F> schemaField = fieldToSchemaField.get(field);
        if (schemaField == null) {
            return null;
        } else {
            return schemaField.getNestedSchema();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataSchema) {
            return DataSchema.equals(this, (DataSchema<?>) o);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if  (hashCode == -1) {
            hashCode = DataSchema.hashCode(this);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return DataSchema.toString(this);
    }

}
