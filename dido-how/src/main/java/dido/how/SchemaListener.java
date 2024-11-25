package dido.how;

import dido.data.DataSchema;

/**
 * Notify when a {@link DataSchema} is available.
 */
public interface SchemaListener {

    void schemaAvailable(DataSchema schema);
}
