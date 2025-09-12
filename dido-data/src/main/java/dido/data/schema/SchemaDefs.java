package dido.data.schema;

import dido.data.DataSchema;

/**
 * Holder for schema definitions that are referenced elsewhere.
 */
public interface SchemaDefs {

    void setSchema(String schemaName, DataSchema schema);

    SchemaRef getSchemaRef(String schemaName);

    static SchemaDefs newInstance() {
        return new SchemaDefsImpl();
    }
}
