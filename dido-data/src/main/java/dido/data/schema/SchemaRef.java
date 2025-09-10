package dido.data.schema;

import dido.data.DataSchema;

import java.util.function.Supplier;

/**
 * Reference to a Schema defined elsewhere.
 *
 * @see SchemaDefs
 */
public interface SchemaRef extends Supplier<DataSchema> {

    String getSchemaName();

}
