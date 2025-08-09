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
     * If the field name is null, the name will be derived from the index. If the name exists it will be
     * made unique
     *
     * @param schemaField The schema field. Must not be null.
     */
    SchemaField addSchemaField(SchemaField schemaField);

    /**
     * Attempt to remove the SchemaField at the given index.
     *
     * @param index The index.
     * @return The removed field, or null if a field of the given index didn't exist.
     */
    SchemaField removeAt(int index);

    /**
     * Attempt to remove the SchemaField of the given name.
     *
     * @param name The field name.
     * @return The removed field, or null if a field of the given name didn't exist.
     */
    SchemaField removeNamed(String name);

    /**
     * Merge a schema giving priority to the type of the priority schema.
     * If the name of the priority field exists in existing schema, the field is
     * replaced at the existing index. If the name of the priority field is not
     * in the existing schema it is added at the end. The indexes of the incoming
     * schema is ignored.
     *
     * @param prioritySchema The schema to take priority in the merge.
     */
    void merge(DataSchema prioritySchema);

    void concat(DataSchema otherSchema);


    DataSchema toSchema();

    static DataSchemaFactory newInstance() {
        return DataSchemaFactory.newInstance();
    }

    static DataSchemaFactory newInstanceFrom(DataSchema originalSchema) {
        return DataSchemaFactory.newInstanceFrom(originalSchema);
    }
}
