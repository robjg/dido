package dido.data.generic;


import dido.data.DataSchema;
import dido.data.SchemaReference;

import java.util.*;

/**
 * Builder for an {@link GenericDataSchema}.
 *
 * @param <F> The field type.
 */
public class GenericSchemaBuilder<F> {

    private final Map<Integer, GenericSchemaField<F>> indexToFields = new TreeMap<>();

    private final Map<F, Integer> fieldToIndex = new HashMap<>();

    private int firstIndex;

    private int lastIndex;

    private GenericSchemaBuilder() {
    }

    public static GenericSchemaBuilder<String> forStringFields() {
        return new GenericSchemaBuilder<>();
    }

    public static <F> GenericSchemaBuilder<F> forFieldType(Class<F> ignored) {
        return new GenericSchemaBuilder<>();
    }

    public static <F> GenericSchemaBuilder<F> impliedType() {
        return new GenericSchemaBuilder<>();
    }

    // Add Simple Fields

    public GenericSchemaBuilder<F> add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public GenericSchemaBuilder<F> addAt(int index, Class<?> fieldType) {
        return addFieldAt(index, null, fieldType);
    }

    public GenericSchemaBuilder<F> addField(F field, Class<?> fieldType) {
        return addFieldAt(0, field, fieldType);
    }

    public GenericSchemaBuilder<F> addFieldAt(int index, F field, Class<?> fieldType) {
        return addGenericSchemaField(GenericSchemaField.of(processIndex(index), field, fieldType));
    }

    // Add Nested Field

    public <N> GenericSchemaBuilder<F> addNested(GenericDataSchema<N> nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public GenericSchemaBuilder<F> addNestedAt(int index,
                                               DataSchema nestedSchema) {
        return addNestedFieldAt(processIndex(index), null, nestedSchema);
    }

    public GenericSchemaBuilder<F> addNestedField(F field,
                                                  DataSchema nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F> addNestedFieldAt(int index,
                                                    F field,
                                                    DataSchema nestedSchema) {

        return addGenericSchemaField(GenericSchemaField.ofNested(
                processIndex(index), field, nestedSchema));
    }

    // Add Nested Reference

    public GenericSchemaBuilder<F> addNested(SchemaReference nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addNestedAt(int index,
                                               SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addNestedField(F field,
                                                  SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(0, field, nestedSchemaRef);
    }

    public <N> GenericSchemaBuilder<F> addNestedFieldAt(int index,
                                                        F field,
                                                        SchemaReference nestedSchemaRef) {
        return addGenericSchemaField(GenericSchemaField.ofNested(
                processIndex(index), field, nestedSchemaRef));
    }

    // Add Repeating Nested Schema

    public <N> GenericSchemaBuilder<F> addRepeating(GenericDataSchema<N> nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public GenericSchemaBuilder<F> addRepeatingAt(int index,
                                                  DataSchema nestedSchema) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchema);
    }

    public GenericSchemaBuilder<F> addRepeatingField(F field,
                                                     DataSchema nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F> addRepeatingFieldAt(int index,
                                                       F field,
                                                       DataSchema nestedSchema) {
        return addGenericSchemaField(GenericSchemaField.ofRepeating(
                processIndex(index), field, nestedSchema));
    }

    // Add Repeating Nested Schema Ref

    public GenericSchemaBuilder<F> addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingAt(int index,
                                                  SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingField(F field,
                                                     SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(0, field, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingFieldAt(int index,
                                                       F field,
                                                       SchemaReference nestedSchemaRef) {
        return addGenericSchemaField(GenericSchemaField.ofRepeating(
                processIndex(index), field, nestedSchemaRef));
    }


    public GenericSchemaBuilder<F> merge(GenericDataSchema<F> prioritySchema) {

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


    public GenericSchemaBuilder<F> addGenericSchemaField(GenericSchemaField<F> schemaField) {

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
