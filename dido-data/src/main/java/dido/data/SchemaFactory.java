package dido.data;

/**
 * Something that can create a {@link DataSchema}. During the schema creation process this factory
 * provides a view of the in progress schema by implementing {@link DataSchema}. This allows things
 * like Transformation Factories to understand the data being transformed to.
 *
 */
public interface SchemaFactory extends DataSchema {

    /**
     * Add a schema field to be included in the created schema. If the index is &lt; 1, the field will be
     * added to the end of the schema definition. If the index exists, the definition will be updated.
     * If the field name is null, the name will be derived from the index.
     *
     * @param schemaField The schema field. Must not be null.
     */
    SchemaField addSchemaField(SchemaField schemaField);

    SchemaField removeAt(int index);

    SchemaField removeNamed(String name);

    void merge(DataSchema prioritySchema);

    DataSchema toSchema();
}
