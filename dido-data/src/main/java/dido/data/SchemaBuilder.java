package dido.data;

import java.util.*;

/**
 * Builder for an {@link DataSchema}.
 *
 * @param <F> The field type.
 */
public class SchemaBuilder<F> {

    private final Map<Integer, SchemaField<F>> indexToFields = new TreeMap<>();

    private final Map<F, Integer> fieldToIndex = new HashMap<>();

    private int firstIndex;

    private int lastIndex;

    private SchemaBuilder() {
    }

    public static SchemaBuilder<String> forStringFields() {
        return new SchemaBuilder<>();
    }

    public static <F> SchemaBuilder<F> forFieldType(Class<F> ignored) {
        return new SchemaBuilder<>();
    }

    public static <F> SchemaBuilder<F> impliedType() {
        return new SchemaBuilder<>();
    }

    // Add Simple Fields

    public SchemaBuilder<F> add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public SchemaBuilder<F> addAt(int index, Class<?> fieldType) {
        return addFieldAt(index, null, fieldType);
    }

    public SchemaBuilder<F> addField(F field, Class<?> fieldType) {
            return addFieldAt(0, field, fieldType);
    }

    public SchemaBuilder<F> addFieldAt(int index, F field, Class<?> fieldType) {
        return addSchemaField(SchemaFields.of(processIndex(index), field, fieldType));
    }

    // Add Nested Field

    public <N> SchemaBuilder<F> addNested(DataSchema<N> nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedAt(int index,
                                            DataSchema<N> nestedSchema) {
        return addNestedFieldAt(processIndex(index), null, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedField(F field,
                                               DataSchema<N> nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedFieldAt(int index,
                                                 F field,
                                                 DataSchema<N> nestedSchema) {

        return addSchemaField(SchemaFields.ofNested(
                processIndex(index), field, nestedSchema));
    }

    // Add Nested Reference

    public <N> SchemaBuilder<F> addNested(SchemaReference<N> nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addNestedAt(int index,
                                            SchemaReference<N> nestedSchemaRef) {
        return addNestedFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addNestedField(F field,
                                               SchemaReference<N> nestedSchemaRef) {
        return addNestedFieldAt(0, field, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addNestedFieldAt(int index,
                                                 F field,
                                                 SchemaReference<N> nestedSchemaRef) {
        return addSchemaField(SchemaFields.ofNested(
                processIndex(index), field, nestedSchemaRef));
    }

    // Add Repeating Nested Schema

    public <N> SchemaBuilder<F> addRepeating(DataSchema<N> nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public <N> SchemaBuilder<F> addRepeatingAt(int index,
                                                     DataSchema<N> nestedSchema) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchema);
    }

    public <N> SchemaBuilder<F> addRepeatingField(F field,
                                                  DataSchema<N> nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public <N> SchemaBuilder<F> addRepeatingFieldAt(int index,
                                                    F field,
                                                    DataSchema<N> nestedSchema) {
        return addSchemaField(SchemaFields.ofRepeating(
                processIndex(index), field, nestedSchema));
    }

    // Add Repeating Nested Schema Ref

    public <N> SchemaBuilder<F> addRepeating(SchemaReference<N> nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addRepeatingAt(int index,
                                                     SchemaReference<N> nestedSchemaRef) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addRepeatingField(F field,
                                                    SchemaReference<N> nestedSchemaRef) {
        return addRepeatingFieldAt(0, field, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addRepeatingFieldAt(int index,
                                                    F field,
                                                    SchemaReference<N> nestedSchemaRef) {
        return addSchemaField(SchemaFields.ofRepeating(
                processIndex(index), field, nestedSchemaRef));
    }

    public SchemaBuilder<F> merge(DataSchema<F> prioritySchema) {

        for (int i = prioritySchema.firstIndex(); i > 0; i = prioritySchema.nextIndex(i)) {

            SchemaField<F> schemaField = prioritySchema.getSchemaFieldAt(i);

            F priorityField = schemaField.getField();
            if (priorityField == null) {
                addSchemaField(schemaField.mapTo(schemaField.getIndex(),
                        Optional.ofNullable(indexToFields.get(schemaField.getIndex()))
                                .map(sf -> sf.getField())
                                .orElse(null)));
            }
            else {
                Integer index = fieldToIndex.get(priorityField);
                if (index == null) {
                    addSchemaField(schemaField.mapToIndex(lastIndex + 1));
                } else {
                    addSchemaField(schemaField.mapToIndex(index));
                }
            }
        }

        return this;
    }

    public SchemaBuilder<F> addSchemaField(SchemaField<F> schemaField) {

        int index = schemaField.getIndex();

        indexToFields.put(index, schemaField);

        F field = schemaField.getField();
        if (field != null) {
            fieldToIndex.put(field, index);
        }

        if (firstIndex == 0 || index < firstIndex) {
            firstIndex = index;
        }

        if (index > lastIndex) {
            lastIndex = index;
        }

        return this;
    }

    public DataSchema<F> build() {
        return new Impl<>(this);
    }

    // Implementation


    private int processIndex(int index) {
        if (index == 0) {
            return ++lastIndex;
        }
        else if (index <= lastIndex) {
            throw new IllegalArgumentException(
                    "Index + " + index + " must be greater than Last index " + lastIndex);
        }
        else {
            lastIndex = index;
            return index;
        }
    }

    static class Impl<F> implements DataSchema<F> {

        private final Class<?>[] indexToType;

        private final Map<F, Integer> fieldToIndex;

        private final F[] indexToField;

        private final int[] nextIndex;

        private final SchemaField<F>[] indexToSchemaField;

        private final int firstIndex;

        private final int lastIndex;

        @SuppressWarnings("unchecked")
        Impl(SchemaBuilder<F> builder) {
            this.fieldToIndex = new LinkedHashMap<>();
            this.firstIndex = builder.firstIndex;
            this.lastIndex = builder.lastIndex;

            Class<?>[] indexToType = new Class<?>[this.lastIndex];
            Object[] indexToField =  new Object[this.lastIndex];
            int[] nextIndex = new int[this.lastIndex];
            SchemaField<F>[] indexToSchemaField = new SchemaField[this.lastIndex];

            int last = 0;
            for (SchemaField<F> meta : builder.indexToFields.values()) {
                int index = meta.getIndex();

                indexToSchemaField[index -1] = meta;
                indexToType[index - 1] = meta.getType();
                indexToField[index - 1] = meta.getField();

                F field = meta.getField();
                if (field != null) {
                    fieldToIndex.put(field, index);
                }

                if (last != 0) {
                    nextIndex[last - 1] = index;
                }
                last = index;
            }

            this.indexToType = indexToType;
            //noinspection unchecked
            this.indexToField = (F[]) indexToField;
            this.nextIndex = nextIndex;
            this.indexToSchemaField = indexToSchemaField;
        }

        @Override
        public SchemaField<F> getSchemaFieldAt(int index) {
            return indexToSchemaField[index - 1];
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return indexToType[index - 1];
        }

        @Override
        public int getIndex(F field) {
            return fieldToIndex.get(field);
        }

        @Override
        public int nextIndex(int index) {
            return this.nextIndex[index - 1];
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
        public Collection<F> getFields() {
            return fieldToIndex.keySet();
        }

        @Override
        public F getFieldAt(int index) {
            return indexToField[index - 1];
        }

        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            SchemaField<F> schemaField = indexToSchemaField[index -1];
            if (schemaField == null) {
                return null;
            } else {
                return schemaField.getNestedSchema();
            }
        }

        @Override
        public <N> DataSchema<N> getSchema(F field) {
            Integer index = fieldToIndex.get(field);
            if (index == null) {
                return null;
            } else {
                return getSchemaAt(index);
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
            return DataSchema.hashCode(this);
        }

        @Override
        public String toString() {
            return DataSchema.toString(this);
        }
    }
}
