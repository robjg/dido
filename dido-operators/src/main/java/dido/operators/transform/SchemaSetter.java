package dido.operators.transform;

import dido.data.SchemaField;

public interface SchemaSetter {

    /**
     * Add a schema field to be included in the created schema. If the index is < 1, the field will be
     * added to the end of the schema definition. If the index exists, the definition will be updated.
     * If the field name is null, the name will be derived from the index.
     *
     * @param schemaField The schema field. Must not be null.
     */
    void addSchemaField(SchemaField schemaField);

    void removeNamed(SchemaField schemaField);

}
