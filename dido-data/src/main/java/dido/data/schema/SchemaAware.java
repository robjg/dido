package dido.data.schema;

import dido.data.DataSchema;

/**
 * Something that wishes to be notified that a new {@link DataSchema} is available.
 */
public interface SchemaAware {

    void schemaAvailable(DataSchema schema);

}
