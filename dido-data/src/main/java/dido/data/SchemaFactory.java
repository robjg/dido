package dido.data;

/**
 * Something that can create a {@link DataSchema}. During the schema creation process this factory
 * provides a view of the in progress schema by implementing {@link DataSchema}. This allows things
 * like Transformation Factories to understand the data being transformed to.
 *
 * @param <S>
 */
public interface SchemaFactory<S extends DataSchema> extends DataSchema {

    void addNamedAt(int index, String field, Class<?> fieldType);

    void addNestedNamedAt(int index,
                          String name,
                          DataSchema nestedSchema);

    void addNestedNamedAt(int index,
                          String name,
                          SchemaReference nestedSchemaRef);

    void addRepeatingNamedAt(int index,
                             String name,
                             DataSchema nestedSchema);

    void addRepeatingNamedAt(int index,
                             String name,
                             SchemaReference nestedSchemaRef);

    void addSchemaField(SchemaField schemaField);

    void removeAt(int index);

    void removeNamed(String name);

    void merge(DataSchema prioritySchema);

    S toSchema();
}
