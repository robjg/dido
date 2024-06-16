package dido.data;

import java.util.*;

/**
 * Builder for an {@link DataSchema}.
 */
public class SchemaBuilder {

    private final Map<Integer, SchemaField> indexToFields = new TreeMap<>();

    private final Map<String, Integer> fieldToIndex = new HashMap<>();

    private int firstIndex;

    private int lastIndex;

    private SchemaBuilder() {
    }

    public static SchemaBuilder newInstance() {
        return new SchemaBuilder();
    }

    // Add Simple Fields

    public SchemaBuilder add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public SchemaBuilder addAt(int index, Class<?> fieldType) {
        return addFieldAt(index, null, fieldType);
    }

    public SchemaBuilder addField(String field, Class<?> fieldType) {
        return addFieldAt(0, field, fieldType);
    }

    public SchemaBuilder addFieldAt(int index, String field, Class<?> fieldType) {
        return addSchemaField(SchemaField.of(processIndex(index), field, fieldType));
    }

    // Add Nested Field

    public SchemaBuilder addNested(DataSchema nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public SchemaBuilder addNestedAt(int index,
                                     DataSchema nestedSchema) {
        return addNestedFieldAt(processIndex(index), null, nestedSchema);
    }

    public SchemaBuilder addNestedField(String field,
                                        DataSchema nestedSchema) {
        return addNestedFieldAt(0, field, nestedSchema);
    }

    public SchemaBuilder addNestedFieldAt(int index,
                                          String field,
                                          DataSchema nestedSchema) {

        return addSchemaField(SchemaField.ofNested(
                processIndex(index), field, nestedSchema));
    }

    // Add Nested Reference

    public SchemaBuilder addNested(SchemaReference nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addNestedAt(int index,
                                     SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public SchemaBuilder addNestedField(String field,
                                        SchemaReference nestedSchemaRef) {
        return addNestedFieldAt(0, field, nestedSchemaRef);
    }

    public SchemaBuilder addNestedFieldAt(int index,
                                          String field,
                                          SchemaReference nestedSchemaRef) {
        return addSchemaField(SchemaField.ofNested(
                processIndex(index), field, nestedSchemaRef));
    }

    // Add Repeating Nested Schema

    public SchemaBuilder addRepeating(DataSchema nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        DataSchema nestedSchema) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchema);
    }

    public SchemaBuilder addRepeatingField(String field,
                                           DataSchema nestedSchema) {
        return addRepeatingFieldAt(0, field, nestedSchema);
    }

    public SchemaBuilder addRepeatingFieldAt(int index,
                                             String field,
                                             DataSchema nestedSchema) {
        return addSchemaField(SchemaField.ofRepeating(
                processIndex(index), field, nestedSchema));
    }

    // Add Repeating Nested Schema Ref

    public SchemaBuilder addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(processIndex(index), null, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingField(String field,
                                           SchemaReference nestedSchemaRef) {
        return addRepeatingFieldAt(0, field, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingFieldAt(int index,
                                             String field,
                                             SchemaReference nestedSchemaRef) {
        return addSchemaField(SchemaField.ofRepeating(
                processIndex(index), field, nestedSchemaRef));
    }


    public SchemaBuilder merge(DataSchema prioritySchema) {

        for (int i = prioritySchema.firstIndex(); i > 0; i = prioritySchema.nextIndex(i)) {

            SchemaField schemaField = prioritySchema.getSchemaFieldAt(i);

            String priorityField = schemaField.getName();
            if (priorityField == null) {
                addSchemaField(schemaField.mapTo(schemaField.getIndex(),
                        Optional.ofNullable(indexToFields.get(schemaField.getIndex()))
                                .map(SchemaField::getName)
                                .orElse(null)));
            } else {
                Integer index = fieldToIndex.get(priorityField);
                addSchemaField(schemaField.mapToIndex(
                        Objects.requireNonNullElseGet(index, () -> lastIndex + 1)));
            }
        }

        return this;
    }

    public SchemaBuilder addSchemaField(SchemaField schemaField) {

        int index = schemaField.getIndex();

        indexToFields.put(index, schemaField);

        String field = schemaField.getName();
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

    public DataSchema build() {
        return DataSchemaImpl.fromFields(indexToFields.values(),
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
