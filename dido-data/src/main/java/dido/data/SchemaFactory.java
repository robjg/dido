package dido.data;

/**
 * Something that can create a {@link DataSchema}. During the schema creation process this factory
 * provides a view of the in progress schema by implementing {@link DataSchema}. This allows things
 * like Transformation Factories to understand the data being transformed to.
 *
 */
public interface SchemaFactory extends DataSchema {

    void addSchemaField(SchemaField schemaField);

    void removeAt(int index);

    void removeNamed(String name);

    void merge(DataSchema prioritySchema);

    DataSchema toSchema();
}
