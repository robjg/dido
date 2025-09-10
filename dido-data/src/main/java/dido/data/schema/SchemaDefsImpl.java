package dido.data.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple implementation of {@link SchemaDefs}.
 */
public class SchemaDefsImpl implements SchemaDefs {

    private final Map<String, SchemaRef> schemas = new HashMap<>();

    @Override
    public void registerSchema(String schemaName, SchemaRef schema) {
        SchemaRef existing = schemas.get(
                Objects.requireNonNull(schemaName, "No Schema Name"));
        if (existing != null) {
            throw new IllegalArgumentException("Schema already registered with name "
                    + schemaName + ": " + existing);
        }
        schemas.put(schemaName, Objects.requireNonNull(schema, "No Schema"));
    }

    @Override
    public SchemaRef resolveSchema(String schemaName) {
        return schemas.get(schemaName);
    }

}
