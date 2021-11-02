package dido.data;

import java.util.*;
import java.util.function.Supplier;

/**
 * Builder for an {@link DataSchema}.
 *
 * @param <F> The field type.
 */
public class SchemaBuilder<F> {

    private final Map<Integer, FieldMeta<?>> indexToMeta = new HashMap<>();

    private final Map<F, Integer> fieldToIndex = new LinkedHashMap<>();

    private final Map<Integer, F> indexToField = new HashMap<>();

    private final Map<Integer, Integer> nextIndex = new HashMap<>();

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

    public SchemaBuilder<F> addNextIndex(Class<?> fieldType) {
        return addIndexedField(0, null, fieldType);
    }

    public SchemaBuilder<F> addField(F field, Class<?> fieldType) {
        return addIndexedField(0, field, fieldType);
    }

    public SchemaBuilder<F> addIndex(int index, Class<?> fieldType) {
        return addIndexedField(index, null, fieldType);
    }

    public SchemaBuilder<F> addIndexedField(int index, F field, Class<?> fieldType) {
        if (isSchemaType(fieldType)) {
            throw new IllegalArgumentException("Don't use this method for nested schemas.");
        }
        return addMetaField(index, field, FieldMeta.from(fieldType));
    }

    //

    public <N> SchemaBuilder<F> addNestedField(F field,
                                               DataSchema<N> nestedSchema) {
        return addNestedIndexedField(0, field, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedIndex(int index,
                                               DataSchema<N> nestedSchema) {
        return addNestedIndexedField(index, null, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedIndexedField(int index, F field,
                                                      DataSchema<N> nestedSchema) {
        return addMetaField(index, field, FieldMeta.from(GenericData.class, nestedSchema));
    }

    //

    public <N> SchemaBuilder<F> addNestedIndex(int index,
                                               Supplier<DataSchema<N>> nestedSchemaRef) {
        return addMetaField(index, null, FieldMeta.from(GenericData.class, nestedSchemaRef));
    }

    public <N> SchemaBuilder<F> addNestedField(F field,
                                               Supplier<DataSchema<N>> nestedSchemaRef) {
        return addMetaField(0, field, FieldMeta.from(GenericData.class, nestedSchemaRef));
    }

    public <N> SchemaBuilder<F> addNestedIndexedField(int index, F field,
                                                      Supplier<DataSchema<N>> nestedSchemaRef) {
        return addMetaField(index, field, FieldMeta.from(GenericData.class, nestedSchemaRef));
    }

    //

    public <N> SchemaBuilder<F> addNestedRepeatingField(F field,
                                                        DataSchema<N> nestedSchema) {
        return addNestedRepeatingIndexedField(0, field, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedRepeatingIndex(int index,
                                                        DataSchema<N> nestedSchema) {
        return addNestedRepeatingIndexedField(index, null, nestedSchema);
    }

    public <N> SchemaBuilder<F> addNestedRepeatingIndexedField(int index, F field,
                                                               DataSchema<N> nestedSchema) {
        return addMetaField(index, field, FieldMeta.from(GenericData[].class, nestedSchema));
    }

    //
    public <N> SchemaBuilder<F> addNestedRepeatingField(F field,
                                                        Supplier<DataSchema<N>> nestedSchemaRef) {
        return addNestedRepeatingIndexedField(0, field, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addNestedRepeatingIndex(int index,
                                                        Supplier<DataSchema<N>> nestedSchemaRef) {
        return addNestedRepeatingIndexedField(index, null, nestedSchemaRef);
    }

    public <N> SchemaBuilder<F> addNestedRepeatingIndexedField(int index, F field,
                                                               Supplier<DataSchema<N>> nestedSchemaRef) {
        return addMetaField(index, field, FieldMeta.from(GenericData[].class, nestedSchemaRef));
    }

    //

    private <N> SchemaBuilder<F> addMetaField(int index, F field, FieldMeta<N> fieldMeta) {
        if (index == 0) {
            index = lastIndex + 1;
        }

        indexToMeta.put(index, fieldMeta);

        if (firstIndex == 0) {
            firstIndex = index;
            lastIndex = index;
        } else {
            if (index <= lastIndex) {
                if (!nextIndex.containsKey(index) && index != lastIndex) {
                    throw new IllegalArgumentException("Index + " + index + " must be greater than Last index " +
                            lastIndex + ", unless overwriting an existing index.");
                }
            } else {
                nextIndex.put(lastIndex, index);
                lastIndex = index;
            }
        }

        if (field != null) {
            fieldToIndex.put(field, index);
            indexToField.put(index, field);
        }

        return this;
    }

    public SchemaBuilder<F> merge(DataSchema<F> prioritySchema) {

        for (int i = prioritySchema.firstIndex(); i > 0; i = prioritySchema.nextIndex(i)) {
            FieldMeta<?> priorityMeta = fieldMetaAt(i, prioritySchema);
            F priorityField = prioritySchema.getFieldAt(i);
            if (priorityField == null) {
                // Not sure what the point of this is.
                addMetaField(0, null, priorityMeta);
            } else {
                Integer existingIndex = fieldToIndex.get(priorityField);
                addMetaField(Objects.requireNonNullElse(existingIndex, 0), priorityField, priorityMeta);
            }
        }
        return this;
    }

    private FieldMeta<?> fieldMetaAt(int index, DataSchema<F> schema) {
        Class<?> fieldType = schema.getTypeAt(index);
        if (isSchemaType(fieldType)) {
            return FieldMeta.from(fieldType, schema.getSchemaAt(index));
        } else {
            return FieldMeta.from(fieldType);
        }
    }

    static class FieldMeta<N> {

        private final Class<?> type;
        private final Supplier<DataSchema<N>> nestedSchemaRef;

        FieldMeta(Class<?> fieldType, Supplier<DataSchema<N>> nestedSchemaRef) {
            this.type = Objects.requireNonNull(fieldType);
            this.nestedSchemaRef = nestedSchemaRef;
        }

        static FieldMeta<?> from(Class<?> fieldType) {
            return new FieldMeta<>(fieldType, null);
        }

        static <N> FieldMeta<N> from(Class<?> fieldType, DataSchema<N> nestedSchema) {
            return new FieldMeta<>(fieldType, () -> nestedSchema);
        }

        static <N> FieldMeta<N> from(Class<?> fieldType, Supplier<DataSchema<N>> nestedSchemaRef) {
            return new FieldMeta<>(fieldType, nestedSchemaRef);
        }
    }

    public DataSchema<F> build() {
        return new Impl<>(this);
    }

    private static boolean isSchemaType(Class<?> type) {
        if (type.isArray()) {
            return isSchemaType(type.getComponentType());
        } else {
            return DataSchema.class.isAssignableFrom(type);
        }
    }

    static class Impl<F> implements DataSchema<F> {

        private final Class<?>[] indexToType;

        private final Map<F, Integer> fieldToIndex;

        private final F[] indexToField;

        private final int[] nextIndex;

        private final Supplier<? extends DataSchema<?>>[] nestedSchemas;

        private final int firstIndex;

        private final int lastIndex;

        Impl(SchemaBuilder<F> builder) {
            this.fieldToIndex = new LinkedHashMap<>(builder.fieldToIndex);
            this.firstIndex = builder.firstIndex;
            this.lastIndex = builder.lastIndex;

            Class<?>[] indexToType = new Class<?>[this.lastIndex];
            Object[] indexToField =  new Object[this.lastIndex];
            int[] nextIndex = new int[this.lastIndex];
            Supplier<? extends DataSchema<?>>[] nestedSchemas = new Supplier[this.lastIndex];

            for (Map.Entry<Integer, FieldMeta<?>> entry : builder.indexToMeta.entrySet()) {
                Integer index = entry.getKey();
                FieldMeta<?> meta = entry.getValue();

                indexToType[index - 1] = meta.type;
                indexToField[index - 1] = builder.indexToField.get(index);
                nextIndex[index - 1] = Objects.requireNonNullElse(
                        builder.nextIndex.get(index), 0);

                if (meta.nestedSchemaRef != null) {
                    nestedSchemas[index - 1] = meta.nestedSchemaRef;
                }
            }

            this.indexToType = indexToType;
            this.indexToField = (F[]) indexToField;
            this.nextIndex = nextIndex;
            this.nestedSchemas = nestedSchemas;
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
        public Class<?> getType(F field) {
            Integer index = fieldToIndex.get(field);
            if (index == null) {
                return null;
            } else {
                return getTypeAt(index);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            Supplier<? extends DataSchema<?>> schemaRef = nestedSchemas[index - 1];
            if (schemaRef == null) {
                return null;
            } else {
                return (DataSchema<N>) schemaRef.get();
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
