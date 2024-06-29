package dido.data;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Builder for an {@link DataSchema}.
 */
public class SchemaBuilder {

    private final NavigableMap<Integer, SchemaField> indexToFields = new TreeMap<>();

    private final Map<String, Integer> nameToIndex = new HashMap<>();

    // TODO: Make configurable.
    private static final UnaryOperator<String> fieldRenameStrategy = s -> s + "_";

    protected SchemaBuilder(DataSchema from) {
        for (SchemaField schemaField : from.getSchemaFields()) {
            indexToFields.put(schemaField.getIndex(), schemaField);
            nameToIndex.put(schemaField.getName(), schemaField.getIndex());
        }
    }

    private SchemaBuilder() {
    }

    public static SchemaBuilder newInstance() {
        return new SchemaBuilder();
    }

    public int firstIndex() {
        return Optional.ofNullable(indexToFields.firstEntry())
                .map(Map.Entry::getKey)
                .orElse(0);

    }

    public int lastIndex() {
        return Optional.ofNullable(indexToFields.lastEntry())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    // Add Simple Fields

    public SchemaBuilder add(Class<?> fieldType) {
        return addAt(0, fieldType);
    }

    public SchemaBuilder addAt(int index, Class<?> fieldType) {
        return addNamedAt(index, null, fieldType);
    }

    public SchemaBuilder addNamed(String name, Class<?> fieldType) {
        return addNamedAt(0, name, fieldType);
    }

    /**
     * Add a field, replacing as necessary.
     * If the index is 0, the next available is used. If the index exists the field is
     * replaced.
     * If the name is null, the name is derived from the index. If the name exists
     * a new name is used.
     *
     * @param index     The index.
     * @param name      The name.
     * @param fieldType The field type. Must not be null.
     * @return This builder
     */
    public SchemaBuilder addNamedAt(int index, String name, Class<?> fieldType) {

        return addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.of(i, n, fieldType)));
    }

    // Add Nested Field

    public SchemaBuilder addNested(DataSchema nestedSchema) {
        return addNestedAt(0, nestedSchema);
    }

    public SchemaBuilder addNestedAt(int index,
                                     DataSchema nestedSchema) {
        return addNestedNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder addNestedNamed(String name,
                                        DataSchema nestedSchema) {
        return addNestedNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder addNestedNamedAt(int index,
                                          String name,
                                          DataSchema nestedSchema) {

        return addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofNested(i, n, nestedSchema)));
    }

    // Add Nested Reference

    public SchemaBuilder addNested(SchemaReference nestedSchemaRef) {
        return addNestedAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addNestedAt(int index,
                                     SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder addNestedNamed(String field,
                                        SchemaReference nestedSchemaRef) {
        return addNestedNamedAt(0, field, nestedSchemaRef);
    }

    public SchemaBuilder addNestedNamedAt(int index,
                                          String name,
                                          SchemaReference nestedSchemaRef) {

        return addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofNested(i, n, nestedSchemaRef)));
    }

    // Add Repeating Nested Schema

    public SchemaBuilder addRepeating(DataSchema nestedSchema) {
        return addRepeatingAt(0, nestedSchema);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        DataSchema nestedSchema) {
        return addRepeatingNamedAt(index, null, nestedSchema);
    }

    public SchemaBuilder addRepeatingNamed(String name,
                                           DataSchema nestedSchema) {
        return addRepeatingNamedAt(0, name, nestedSchema);
    }

    public SchemaBuilder addRepeatingNamedAt(int index,
                                             String name,
                                             DataSchema nestedSchema) {

        return addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofRepeating(i, n, nestedSchema)));
    }

    // Add Repeating Nested Schema Ref

    public SchemaBuilder addRepeating(SchemaReference nestedSchemaRef) {
        return addRepeatingAt(0, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingAt(int index,
                                        SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(index, null, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingNamed(String name,
                                           SchemaReference nestedSchemaRef) {
        return addRepeatingNamedAt(0, name, nestedSchemaRef);
    }

    public SchemaBuilder addRepeatingNamedAt(int index,
                                             String name,
                                             SchemaReference nestedSchemaRef) {

        return addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofRepeating(i, n, nestedSchemaRef)));
    }

    public SchemaBuilder removeAt(int index) {

        SchemaField schemaField = this.indexToFields.remove(index);
        if (schemaField == null) {
            throw new IllegalArgumentException("No field at index " + index);
        }
        this.nameToIndex.remove(schemaField.getName());
        return this;

    }

    public SchemaBuilder removeNamed(String name) {

        Integer index = nameToIndex.get(name);
        if (index == null) {
            throw new IllegalArgumentException("No field named " + name);
        }

        return removeAt(index);
    }

    /**
     * Merge a schema giving priority to the type of the priority schema.
     * If the name of the priority field exists in existing schema, the field is
     * replaced at the existing index. If the name of the priority field is not
     * in the existing schema it is added at the end. The indexes of the incoming
     * schema is ignored.
     *
     * @param prioritySchema The schema to take priority in the merge.
     * @return This builder.
     */
    public SchemaBuilder merge(DataSchema prioritySchema) {

        for (int i = prioritySchema.firstIndex(); i > 0; i = prioritySchema.nextIndex(i)) {

            SchemaField schemaField = prioritySchema.getSchemaFieldAt(i);

            String priorityName = schemaField.getName();
            Integer existingIndex = nameToIndex.get(priorityName);
            if (existingIndex == null) {
                addSchemaField(schemaField.mapToIndex(lastIndex() + 1));
            } else {
                SchemaField newField = schemaField.mapToIndex(existingIndex);
                indexToFields.put(existingIndex, newField);
            }
        }

        return this;
    }

    /**
     * Add a schema field. If the index exists, we replace its field with
     * the new field.
     *
     * @param schemaField The field. Never null.
     * @return This builder.
     */
    public SchemaBuilder addSchemaField(SchemaField schemaField) {

        int index = schemaField.getIndex();

        SchemaField existingField = indexToFields.remove(index);
        if (existingField == null) {

            // Ensure name is unique.
            String name = schemaField.getName();
            if (nameToIndex.containsKey(name)) {
                return addSchemaField(schemaField.mapToFieldName(
                        fieldRenameStrategy.apply(name)));
            }

            indexToFields.put(index, schemaField);
            nameToIndex.put(name, index);

            return this;
        } else {
            nameToIndex.remove(existingField.getName());
            return addSchemaField(schemaField);
        }
    }


    public DataSchema build() {
        return DataSchemaImpl.fromFields(indexToFields.values(),
                firstIndex(), lastIndex());
    }

    // Implementation

    /**
     * If the index is 0 we're finding the next available index. We're also remembering
     * what that is for next time.
     *
     * @param index The index;
     * @return The same index, or the next available.
     */
    private <T> T schemaFieldOf(int index, String name, SchemaFieldFunc<T> func) {
         index = index == 0 ? lastIndex() + 1 : index;
         return func.apply(index, name == null ?  nameForIndex(index): name);
    }

    interface SchemaFieldFunc<T> {

        T apply(int index, String field);
    }

    public static String nameForIndex(int index) {
        return "[" + index + "]";
    }
}
