package dido.data;

import java.util.*;
import java.util.function.Supplier;

/**
 * Builder for an {@link DataSchema}.
 *
 * @param <F> The field type.
 */
public class SchemaBuilder<F> {

    private final Class<F> fieldType;

    private final Map<Integer, FieldMeta<?>> indexToMeta = new HashMap<>();

    private final Map<F, Integer> fieldToIndex = new LinkedHashMap<>();

    private final Map<Integer, F> indexToField = new HashMap<>();

    private final Map<Integer, Integer> nextIndex = new HashMap<>();

    private int firstIndex;

    private int lastIndex;

    public SchemaBuilder(Class<F> fieldType) {
        this.fieldType = Objects.requireNonNull(fieldType);
    }

    public static SchemaBuilder<String> forStringFields() {
        return new SchemaBuilder<>(String.class);
    }

    public static <F> SchemaBuilder<F> forFieldType(Class<F> fieldType) {
        return new SchemaBuilder<>(fieldType);
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
                if (!nextIndex.containsKey(index)) {
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
                if (existingIndex == null) {
                    addMetaField(0, priorityField, priorityMeta);
                } else {
                    addMetaField(existingIndex, priorityField, priorityMeta);
                }
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

        private final Class<F> fieldType;

        private final Map<Integer, Class<?>> indexToType;

        private final Map<F, Integer> fieldToIndex;

        private final Map<Integer, F> indexToField;

        private final Map<Integer, Integer> nextIndex;

        private final Map<Integer, Supplier<? extends DataSchema<?>>> nestedSchemas;

        private final int firstIndex;

        private final int lastIndex;

        Impl(SchemaBuilder<F> builder) {
            this.fieldType = builder.fieldType;
            this.indexToType = new HashMap<>();
            this.fieldToIndex = new LinkedHashMap<>(builder.fieldToIndex);
            this.indexToField = new HashMap<>(builder.indexToField);
            this.nextIndex = new HashMap<>(builder.nextIndex);
            this.nestedSchemas = new HashMap<Integer, Supplier<? extends DataSchema<?>>>();
            this.firstIndex = builder.firstIndex;
            this.lastIndex = builder.lastIndex;

            for (Map.Entry<Integer, FieldMeta<?>> entry : builder.indexToMeta.entrySet()) {
                Integer index = entry.getKey();
                FieldMeta<?> meta = entry.getValue();
                indexToType.put(index, meta.type);
                if (meta.nestedSchemaRef != null) {
                    nestedSchemas.put(index, meta.nestedSchemaRef);
                }
            }
        }

        @Override
        public Class<F> getFieldType() {
            return fieldType;
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return indexToType.get(index);
        }

        @Override
        public int getIndex(F field) {
            return fieldToIndex.get(field);
        }

        @Override
        public int nextIndex(int index) {
            Integer nextIndex = this.nextIndex.get(index);
            if (nextIndex == null) {
                return 0;
            } else {
                return nextIndex;
            }
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
            return indexToField.get(index);
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
            Supplier<? extends DataSchema<?>> schemaRef = nestedSchemas.get(index);
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
            return "DidoSchema" + indexToField;
        }
    }
}
