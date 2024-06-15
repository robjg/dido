package dido.data;

import java.util.*;

/**
 * Builder for an {@link GenericDataSchema}.
 *
 * @param <F> The field type.
 */
public class SchemaBuilder<F> {

    private final Map<Integer, GenericSchemaField<F>> indexToFields = new TreeMap<>();

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
        return addGenericSchemaField(GenericSchemaFields.of(processIndex(index), field, fieldType));
    }

    // Add Nested Field

    public <N> SchemaBuilder<F> addNested(GenericDataSchema<N> nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public SchemaBuilder<F> addNestedAt(int index,
                                        DataSchema nestedSchema) {
        return addNestedFieldAt(processIndex(index), null, nestedSchema);
    }

    public SchemaBuilder<F> addNestedField(F field,
                                           DataSchema nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public SchemaBuilder<F> addNestedFieldAt(int index,
                                             F field,
                                             DataSchema nestedSchema) {

        return addGenericSchemaField(GenericSchemaFields.ofNested(
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
        return addGenericSchemaField(GenericSchemaFields.ofNested(
                processIndex(index), field, nestedSchemaRef));
    }

    // Add Repeating Nested Schema

    public <N> SchemaBuilder<F> addRepeating(GenericDataSchema<N> nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public SchemaBuilder<F> addRepeatingAt(int index,
                                           DataSchema nestedSchema) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchema);
    }

    public SchemaBuilder<F> addRepeatingField(F field,
                                              DataSchema nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public SchemaBuilder<F> addRepeatingFieldAt(int index,
                                                F field,
                                                DataSchema nestedSchema) {
        return addGenericSchemaField(GenericSchemaFields.ofRepeating(
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
        return addGenericSchemaField(GenericSchemaFields.ofRepeating(
                processIndex(index), field, nestedSchemaRef));
    }

    public SchemaBuilder<F> merge(GenericDataSchema<F> prioritySchema) {

        for (int i = prioritySchema.firstIndex(); i > 0; i = prioritySchema.nextIndex(i)) {

            GenericSchemaField<F> schemaField = prioritySchema.getSchemaFieldAt(i);

            F priorityField = schemaField.getField();
            if (priorityField == null) {
                addGenericSchemaField(schemaField.mapTo(schemaField.getIndex(),
                        Optional.ofNullable(indexToFields.get(schemaField.getIndex()))
                                .map(GenericSchemaField::getField)
                                .orElse(null)));
            } else {
                Integer index = fieldToIndex.get(priorityField);
                addGenericSchemaField(schemaField.mapToIndex(
                        Objects.requireNonNullElseGet(index, () -> lastIndex + 1)));
            }
        }

        return this;
    }

    public SchemaBuilder<String> addSchemaField(SchemaField schemaField) {

        // TODO: Do better.

        if (schemaField.isRepeating()) {
            return ((SchemaBuilder<String>) this)
                    .addGenericSchemaField(GenericSchemaField.ofRepeating(
                            schemaField.getIndex(),
                            schemaField.getName(),
                            schemaField.getNestedSchema()));

        }
        else if (schemaField.isNested()) {
            return ((SchemaBuilder<String>) this)
                    .addGenericSchemaField(GenericSchemaField.ofNested(
                            schemaField.getIndex(),
                            schemaField.getName(),
                            schemaField.getNestedSchema()));
        }
        else {
            return ((SchemaBuilder<String>) this)
                    .addGenericSchemaField(GenericSchemaField.of(
                            schemaField.getIndex(),
                            schemaField.getName(),
                            schemaField.getType()));
        }
    }

    public SchemaBuilder<F> addGenericSchemaField(GenericSchemaField<F> schemaField) {

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

    public int getLastIndex() {
        return lastIndex;
    }

    public GenericDataSchema<F> build() {
        return GenericSchemaImpl.fromFields(indexToFields.values(),
                firstIndex, lastIndex);
    }

    // Implementation


    private int processIndex(int index) {
        if (index == 0) {
            return ++lastIndex;
        } else if (index <= lastIndex) {
            throw new IllegalArgumentException(
                    "Index [" + index + "] must be greater than Last index [" + lastIndex + "]");
        } else {
            lastIndex = index;
            return index;
        }
    }
}
