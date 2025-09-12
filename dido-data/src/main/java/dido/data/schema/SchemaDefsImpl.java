package dido.data.schema;

import dido.data.DataSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple implementation of {@link SchemaDefs}.
 */
public class SchemaDefsImpl implements SchemaDefs {

    private final Map<String, SchemaRef> schemas = new HashMap<>();

    @Override
    public void setSchema(String schemaName, DataSchema schema) {
        SchemaRefImpl existing = (SchemaRefImpl) getSchemaRef(schemaName);
        if (existing.isResolved()) {
            throw new IllegalArgumentException("Schema already registered with name "
                    + schemaName + ": " + existing);
        }
        existing.set(Objects.requireNonNull(schema, "No Schema"));
    }

    @Override
    public SchemaRef getSchemaRef(String schemaName) {
        SchemaRef ref = schemas.get(Objects.requireNonNull(schemaName, "No Schema Name"));
        if (ref == null) {
            ref = SchemaRefImpl.named(schemaName);
            schemas.put(schemaName, ref);
        }
        return ref;
    }

}
