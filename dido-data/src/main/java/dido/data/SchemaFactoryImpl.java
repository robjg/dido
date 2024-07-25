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
    public boolean hasIndex(int index) {
        return indexToFields.containsKey(index);
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
    public boolean hasNamed(String name) {
        return nameToIndex.containsKey(name);
    }

    @Override
    public int getIndexNamed(String name) {
        Integer index = nameToIndex.get(name);
        return index == null ? 0 : index;
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
        if (index == 0) {
            index = lastIndex() + 1;
            schemaField = schemaField.mapToIndex(index);
        }
        if (schemaField.getName() == null || schemaField.getName().isBlank()) {
            schemaField = schemaField.mapToFieldName(DataSchema.nameForIndex(index));
        }

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
