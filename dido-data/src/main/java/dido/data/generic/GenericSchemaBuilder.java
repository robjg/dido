package dido.data.generic;


import dido.data.DataSchema;
import dido.data.SchemaReference;

import java.util.*;
import java.util.function.Function;

/**
 * Builder for an {@link GenericDataSchema}.
 *
 * @param <F> The field type.
 */
public class GenericSchemaBuilder<F> {

    private final GenericSchemaField.Of<F> genericField;

    private final Map<Integer, GenericSchemaField<F>> indexToFields = new TreeMap<>();

    private final Map<F, Integer> fieldToIndex = new HashMap<>();

    private int firstIndex;

    private int lastIndex;

    private GenericSchemaBuilder(Function<? super String, ? extends F> fieldMappingFunc) {
        this.genericField = GenericSchemaField.with(fieldMappingFunc);
    }

    public static GenericSchemaBuilder<String> forStringFields() {
        return new GenericSchemaBuilder<>(Function.identity());
    }

    public static <F> GenericSchemaBuilder<F> forFieldType(Class<F> ignored,
                                                           Function<? super String, ? extends F> fieldMappingFunc) {
        return new GenericSchemaBuilder<>(fieldMappingFunc);
    }

    public static <F> GenericSchemaBuilder<F> impliedType(Function<? super String, ? extends F> fieldMappingFunc) {
        return new GenericSchemaBuilder<>(fieldMappingFunc);
    }

    // Add Simple Fields

    public GenericSchemaBuilder<F> addField(F field, Class<?> fieldType) {
        return addFieldAt(0, field, fieldType);
    }

    public GenericSchemaBuilder<F> addFieldAt(int index, F field, Class<?> fieldType) {
        return addGenericSchemaField(genericField.of(index, field, fieldType));
    }

    // Add Nested Field

    public GenericSchemaBuilder<F> addNestedField(F field,
                                                  DataSchema nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F> addNestedFieldAt(int index,
                                                    F field,
                                                    DataSchema nestedSchema) {

        return addGenericSchemaField(genericField.ofNested(index, field, nestedSchema));
    }

    // Add Nested Reference

    public GenericSchemaBuilder<F> addNestedField(F field,
                                                  SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(0, field, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addNestedFieldAt(int index,
                                                        F field,
                                                        SchemaReference nestedSchemaRef) {
        return addGenericSchemaField(genericField.ofNested(index, field, nestedSchemaRef));
    }

    // Add Repeating Nested Schema

    public GenericSchemaBuilder<F> addRepeatingField(F field,
                                                     DataSchema nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public GenericSchemaBuilder<F> addRepeatingFieldAt(int index,
                                                       F field,
                                                       DataSchema nestedSchema) {
        return addGenericSchemaField(genericField.ofRepeating(index, field, nestedSchema));
    }

    // Add Repeating Nested Schema Ref

    public GenericSchemaBuilder<F> addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingAt(int index,
                                                  SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(index, null, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingField(F field,
                                                     SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(0, field, nestedSchemaRef);
    }

    public GenericSchemaBuilder<F> addRepeatingFieldAt(int index,
                                                       F field,
                                                       SchemaReference nestedSchemaRef) {
        return addGenericSchemaField(genericField.ofRepeating(index, field, nestedSchemaRef));
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

        if (index == 0) {
            index = ++lastIndex;
            schemaField = schemaField.mapToIndex(index);
        }

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

}
