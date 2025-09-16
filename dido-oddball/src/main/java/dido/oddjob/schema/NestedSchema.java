package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.schema.SchemaDefs;
import org.oddjob.arooa.convert.ArooaConversionException;

/**
 * Defines a Nested Schema configuration for use in a {@link SchemaFieldBean}.
 */
public interface NestedSchema {

    DataSchema toSchema(SchemaDefs defs) throws ArooaConversionException;
}
