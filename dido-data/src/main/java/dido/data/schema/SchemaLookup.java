package dido.data.schema;

import dido.data.DataSchema;

/**
 * Provide the ability to find a {@link DataSchema} by name.
 */
public interface SchemaLookup {

    DataSchema getSchema(String schemaName);

}
