package dido.data.schema;

/**
 * Something that is able to notify when a Schema becomes available.
 */
public interface SchemaNotifier {

    void addSchemaListener(SchemaAware schemaAware);

    void removeSchemaListener(SchemaAware schemaAware);
}
