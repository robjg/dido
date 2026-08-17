package dido.data.schema;

import dido.data.DataSchema;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Helper class for {@link SchemaNotifier}s.
 */
public class SchemaListeners implements SchemaNotifier, SchemaAware {

    private final List<SchemaAware> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void schemaAvailable(DataSchema schema) {
        listeners.forEach(l -> l.schemaAvailable(schema));
    }

    @Override
    public void addSchemaListener(SchemaAware schemaAware) {
        listeners.add(schemaAware);
    }

    @Override
    public void removeSchemaListener(SchemaAware schemaAware) {
        listeners.remove(schemaAware);
    }
}
