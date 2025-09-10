package dido.data.schema;

/**
 * Holder for schema definitions that are referenced elsewhere.
 */
public interface SchemaDefs {

    void registerSchema(String schemaName, SchemaRef schemaRef);

    SchemaRef resolveSchema(String schemaName);

}
