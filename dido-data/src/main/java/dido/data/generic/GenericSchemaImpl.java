package dido.data.generic;

import dido.data.DataSchema;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenericSchemaImpl<F> extends AbstractGenericDataSchema<F> {

    private final Class<F> fieldType;

    private final Map<F, GenericSchemaField<F>> fieldToSchemaField;

    private final Map<String, GenericSchemaField<F>> nameToSchemaField;

    private final F[] indexToField;

    private final int[] nextIndex;

    private final GenericSchemaField<F>[] indexToSchemaField;

    private final int firstIndex;

    private final int lastIndex;

    private volatile int hashCode = -1;

    protected GenericSchemaImpl(Class<F> fieldType,
                                GenericDataSchema<F> schema) {
        this(fieldType, schema.getGenericSchemaFields(), schema.firstIndex(), schema.lastIndex());
    }

    protected GenericSchemaImpl(Class<F> fieldType,
                                Iterable<GenericSchemaField<F>> schemaFields,
                                int firstIndex,
                                int lastIndex) {

        this.fieldType = fieldType;

        this.fieldToSchemaField = new LinkedHashMap<>();
        this.nameToSchemaField = new LinkedHashMap<>();

        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;

        int offset = firstIndex - 1;
        Object[] indexToField =  new Object[lastIndex - offset];
        int[] nextIndex = new int[lastIndex - offset];
        GenericSchemaField<F>[] indexToSchemaField = new GenericSchemaField[lastIndex - offset];

        int last = 0;
        for (GenericSchemaField<F> meta : schemaFields) {
            int index = meta.getIndex();

            indexToSchemaField[index - firstIndex] = meta;
            indexToField[index - firstIndex] = meta.getField();

            F field = meta.getField();
            if (field != null) {
                fieldToSchemaField.put(field, meta);
                String name = meta.getName();
                if (name == null) {
                    throw new IllegalStateException("Name for field " + field + " must not be null.");
                }
                nameToSchemaField.put(name, meta);
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

    public static <F> GenericDataSchema<F> fromFields(Class<F> fieldType,
                                                      GenericSchemaField<F>... schemaFields) {

        if (schemaFields.length == 0) {
            return GenericDataSchema.emptySchema(fieldType);
        }
        else {
            return new GenericSchemaImpl<>(fieldType,
                    () -> Arrays.stream(schemaFields).iterator(),
                    schemaFields[0].getIndex(), schemaFields[schemaFields.length - 1].getIndex());
        }
    }


    public static <F> GenericDataSchema<F> fromFields(Class<F> fieldType,
                                                      Iterable<GenericSchemaField<F>> schemaFields,
                                                      int firstIndex,
                                                      int lastIndex) {

        return new GenericSchemaImpl<>(fieldType, schemaFields, firstIndex, lastIndex);
    }

    @Override
    public Class<F> getFieldType() {
        return fieldType;
    }

    @Override
    public boolean hasField(F field) {
        return fieldToSchemaField.containsKey(field);
    }

    @Override
    public boolean hasIndex(int index) {
        return index > 0 && index < lastIndex && indexToSchemaField[index - 1] != null;
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
    public GenericSchemaField<F> getSchemaFieldAt(int index) {
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
    public DataSchema getSchemaAt(int index) {
        GenericSchemaField<F> schemaField = indexToSchemaField[index - firstIndex];
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public Collection<F> getFields() {
        return fieldToSchemaField.keySet();
    }

    @Override
    public boolean hasNamed(String name) {
        return nameToSchemaField.containsKey(name);
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldOf(F field) {
        return fieldToSchemaField.get(field);
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldNamed(String name) {
        return nameToSchemaField.get(name);
    }

    @Override
    public F getFieldNamed(String name) {
        GenericSchemaField<F> schemaField = nameToSchemaField.get(name);
        return schemaField == null ? null : schemaField.getField();
    }

    @Override
    public int getIndexNamed(String name) {
        GenericSchemaField<F> schemaField = nameToSchemaField.get(name);
        return schemaField == null ? 0 : schemaField.getIndex();
    }

    @Override
    public Collection<GenericSchemaField<F>> getGenericSchemaFields() {
        return Arrays.asList(indexToSchemaField);
    }

    @Override
    public int hashCode() {
        if  (hashCode == -1) {
            hashCode = DataSchema.hashCode(this);
        }
        return hashCode;
    }

}
