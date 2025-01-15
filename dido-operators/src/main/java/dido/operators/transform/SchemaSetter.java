package dido.operators.transform;

import dido.data.SchemaFactory;
import dido.data.SchemaField;

/**
 * A simplified view of an {@link dido.data.SchemaFactory} making it easier to build Transformations.
 */
public interface SchemaSetter {

    /**
     * Add a schema field to be included in the created schema. If the index is < 1, the field will be
     * added to the end of the schema definition. If the index exists, the definition will be updated.
     * If the field name is null, the name will be derived from the index.
     *
     * @param schemaField The schema field. Must not be null.
     */
    void addField(SchemaField schemaField);

    void removeField(SchemaField schemaField);

    static SchemaSetter fromSchemaFactory(SchemaFactory schemaFactory ) {

        return new SchemaSetter() {
            @Override
            public void addField(SchemaField schemaField) {
                schemaFactory.addSchemaField(schemaField);
            }

            @Override
            public void removeField(SchemaField schemaField) {
                schemaFactory.removeNamed(schemaField.getName());
            }
        };
    }
}
