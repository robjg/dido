package dido.data.schema;

import dido.data.DataSchema;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper class for {@link SchemaNotifier}s.
 */
public class SchemaTrackers implements SchemaNotifier, SchemaTracker {

    private final List<SchemaTracker> listeners = new CopyOnWriteArrayList<>();

    private final AtomicReference<DataSchema> lastSchema = new AtomicReference<>();

    @Override
    public void schemaAvailable(DataSchema schema) {
        lastSchema.set(schema);
        listeners.forEach(l -> l.schemaAvailable(schema));
    }

    @Override
    public void addSchemaTracker(SchemaTracker schemaTracker) {
        DataSchema last = lastSchema.get();
        if (last != null) {
            schemaTracker.schemaAvailable(last);
        }
        listeners.add(schemaTracker);
    }

    @Override
    public void removeSchemaTracker(SchemaTracker schemaTracker) {
        listeners.remove(schemaTracker);
    }
}
