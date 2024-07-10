package dido.data;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Creates a simple {@link DataSchema} or acts as a base class for {@link SchemaFactory}s that
 * create more complicated Data Schemas.
 *
 * @param <S> The Data Schema Type this factory will create.
 */
public class SchemaFactoryImpl<S extends DataSchema> extends AbstractDataSchema
        implements DataSchema, SchemaFactory<S> {

    /**
     * Passed by subclasses to create the actual schema.
     *
     * @param <S> The type of schema that will be created.
     */
    @FunctionalInterface
    public interface CreationFunction<S extends DataSchema> {

        S create(Collection<SchemaField> fields, int firstIndex, int lastIndex);
    }

    private final CreationFunction<S> creationFunction;

    private final NavigableMap<Integer, SchemaField> indexToFields = new TreeMap<>();

    private final Map<String, Integer> nameToIndex = new HashMap<>();

    // TODO: Make configurable.
    private static final UnaryOperator<String> fieldRenameStrategy = s -> s + "_";


    protected SchemaFactoryImpl(CreationFunction<S> creationFunction) {
        this.creationFunction = creationFunction;
    }

    protected SchemaFactoryImpl(CreationFunction<S> creationFunction, DataSchema from) {
        this.creationFunction = creationFunction;
        for (SchemaField schemaField : from.getSchemaFields()) {
            indexToFields.put(schemaField.getIndex(), schemaField);
            nameToIndex.put(schemaField.getName(), schemaField.getIndex());
        }
    }

    public static SchemaFactory<DataSchema> newInstance() {
            return new SchemaFactoryImpl<>(DataSchemaImpl::fromFields);
    }

    @Override
    public int firstIndex() {
        return Optional.ofNullable(indexToFields.firstEntry())
                .map(Map.Entry::getKey)
                .orElse(0);

    }

    @Override
    public int lastIndex() {
        return Optional.ofNullable(indexToFields.lastEntry())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public int nextIndex(int index) {
        Integer next = indexToFields.higherKey(index);
        return next == null ? 0 : next;
    }

    @Override
    public SchemaField getSchemaFieldAt(int index) {
        return indexToFields.get(index);
    }

    @Override
    public Collection<SchemaField> getSchemaFields() {
        return indexToFields.values();
    }

    @Override
    public int getIndexNamed(String fieldName) {
        return nameToIndex.get(fieldName);
    }

    // Add Simple Fields

    public void add(Class<?> fieldType) {
        addAt(0, fieldType);
    }

    public void addAt(int index, Class<?> fieldType) {
        addNamedAt(index, null, fieldType);
    }

    public void addNamed(String name, Class<?> fieldType) {
        addNamedAt(0, name, fieldType);
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
     */
    @Override
    public void addNamedAt(int index, String name, Class<?> fieldType) {

        addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.of(i, n, fieldType)));
    }

    // Add Nested Field

    public void addNested(DataSchema nestedSchema) {
        addNestedAt(0, nestedSchema);
    }

    public void addNestedAt(int index,
                                     DataSchema nestedSchema) {
        addNestedNamedAt(index, null, nestedSchema);
    }

    public void addNestedNamed(String name,
                                        DataSchema nestedSchema) {
        addNestedNamedAt(0, name, nestedSchema);
    }

    @Override
    public void addNestedNamedAt(int index,
                                          String name,
                                          DataSchema nestedSchema) {

        addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofNested(i, n, nestedSchema)));
    }

    // Add Nested Reference

    public void addNested(SchemaReference nestedSchemaRef) {
        addNestedAt(0, nestedSchemaRef);
    }

    public void addNestedAt(int index,
                                     SchemaReference nestedSchemaRef) {
        addNestedNamedAt(index, null, nestedSchemaRef);
    }

    public void addNestedNamed(String field,
                                        SchemaReference nestedSchemaRef) {
        addNestedNamedAt(0, field, nestedSchemaRef);
    }

    @Override
    public void addNestedNamedAt(int index,
                                          String name,
                                          SchemaReference nestedSchemaRef) {

        addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofNested(i, n, nestedSchemaRef)));
    }

    // Add Repeating Nested Schema

    public void addRepeating(DataSchema nestedSchema) {
        addRepeatingAt(0, nestedSchema);
    }

    public void addRepeatingAt(int index,
                                        DataSchema nestedSchema) {
        addRepeatingNamedAt(index, null, nestedSchema);
    }

    public void addRepeatingNamed(String name,
                                           DataSchema nestedSchema) {
        addRepeatingNamedAt(0, name, nestedSchema);
    }

    @Override
    public void addRepeatingNamedAt(int index,
                                             String name,
                                             DataSchema nestedSchema) {

        addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofRepeating(i, n, nestedSchema)));
    }

    // Add Repeating Nested Schema Ref

    public void addRepeating(SchemaReference nestedSchemaRef) {
        addRepeatingAt(0, nestedSchemaRef);
    }

    public void addRepeatingAt(int index,
                                        SchemaReference nestedSchemaRef) {
        addRepeatingNamedAt(index, null, nestedSchemaRef);
    }

    public void addRepeatingNamed(String name,
                                           SchemaReference nestedSchemaRef) {
        addRepeatingNamedAt(0, name, nestedSchemaRef);
    }

    @Override
    public void addRepeatingNamedAt(int index,
                                             String name,
                                             SchemaReference nestedSchemaRef) {

        addSchemaField(schemaFieldOf(index, name,
                (i, n) -> SchemaField.ofRepeating(i, n, nestedSchemaRef)));
    }

    @Override
    public void removeAt(int index) {

        SchemaField schemaField = this.indexToFields.remove(index);
        if (schemaField == null) {
            throw new IllegalArgumentException("No field at index " + index);
        }
        this.nameToIndex.remove(schemaField.getName());
    }

    @Override
    public void removeNamed(String name) {

        Integer index = nameToIndex.get(name);
        if (index == null) {
            throw new IllegalArgumentException("No field named " + name);
        }
        removeAt(index);
    }

    /**
     * Merge a schema giving priority to the type of the priority schema.
     * If the name of the priority field exists in existing schema, the field is
     * replaced at the existing index. If the name of the priority field is not
     * in the existing schema it is added at the end. The indexes of the incoming
     * schema is ignored.
     *
     * @param prioritySchema The schema to take priority in the merge.
     */
    public void merge(DataSchema prioritySchema) {

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
    }

    /**
     * Add a schema field. If the index exists, we replace its field with
     * the new field.
     *
     * @param schemaField The field. Never null.
     */
    @Override
    public void addSchemaField(SchemaField schemaField) {

        int index = schemaField.getIndex();

        SchemaField existingField = indexToFields.remove(index);
        if (existingField == null) {

            // Ensure name is unique.
            String name = schemaField.getName();
            if (nameToIndex.containsKey(name)) {
                addSchemaField(schemaField.mapToFieldName(
                        fieldRenameStrategy.apply(name)));
            }
            else {
                indexToFields.put(index, schemaField);
                nameToIndex.put(name, index);
            }
        } else {
            nameToIndex.remove(existingField.getName());
            addSchemaField(schemaField);
        }
    }


    @Override
    public S toSchema() {
        return creationFunction.create(indexToFields.values(), firstIndex(), lastIndex());
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
        return func.apply(index, name == null ?  DataSchema.nameForIndex(index): name);
    }

    interface SchemaFieldFunc<T> {

        T apply(int index, String field);
    }
}
